package com.prupe.mcpatcher;

import com.prupe.mcpatcher.TexturePackAPI$1;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import net.minecraft.src.AbstractResourcePack;
import net.minecraft.src.AbstractTexture;
import net.minecraft.src.DefaultResourcePack;
import net.minecraft.src.DynamicTexture;
import net.minecraft.src.FallbackResourceManager;
import net.minecraft.src.FileResourcePack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.ResourcePack;
import net.minecraft.src.SimpleReloadableResourceManager;
import net.minecraft.src.TextureManager;
import net.minecraft.src.TextureMap;
import net.minecraft.src.TextureObject;
import org.lwjgl.opengl.GL11;

public class TexturePackAPI {
	private static final MCLogger logger = MCLogger.getLogger("Texture Pack");
	public static final String DEFAULT_NAMESPACE = "minecraft";
	public static final String MCPATCHER_SUBDIR = "mcpatcher/";
	public static TexturePackAPI instance = new TexturePackAPI();

	public static List<ResourcePack> getResourcePacks(String namespace) {
		ArrayList list = new ArrayList();
		ResourceManager resourceManager = getResourceManager();

		if (resourceManager instanceof SimpleReloadableResourceManager) {
			Iterator i$ = ((SimpleReloadableResourceManager)resourceManager).domainResourceManagers.entrySet().iterator();

			while (i$.hasNext()) {
				Entry entry = (Entry)i$.next();

				if (namespace == null || namespace.equals(entry.getKey())) {
					FallbackResourceManager resourceManager1 = (FallbackResourceManager)entry.getValue();
					list.addAll(resourceManager1.resourcePacks);
				}
			}
		}

		Collections.reverse(list);
		return list;
	}

	public static Set<String> getNamespaces() {
		HashSet set = new HashSet();
		ResourceManager resourceManager = getResourceManager();

		if (resourceManager instanceof SimpleReloadableResourceManager) {
			set.addAll(((SimpleReloadableResourceManager)resourceManager).domainResourceManagers.keySet());
		}

		return set;
	}

	public static ResourceManager getResourceManager() {
		return Minecraft.getMinecraft().getResourceManager();
	}

	public static boolean isDefaultTexturePack() {
		return getResourcePacks("minecraft").size() <= 1;
	}

	public static InputStream getInputStream(ResourceLocation resource) {
		return resource == null ? null : instance.getInputStreamImpl(resource);
	}

	public static boolean hasResource(ResourceLocation resource) {
		if (resource == null) {
			return false;
		} else if (resource.getResourcePath().endsWith(".png")) {
			return getImage(resource) != null;
		} else if (resource.getResourcePath().endsWith(".properties")) {
			return getProperties(resource) != null;
		} else {
			InputStream is = getInputStream(resource);
			MCPatcherUtils.close((Closeable)is);
			return is != null;
		}
	}

	public static BufferedImage getImage(ResourceLocation resource) {
		return resource == null ? null : instance.getImageImpl(resource);
	}

	public static Properties getProperties(ResourceLocation resource) {
		Properties properties = new Properties();
		return getProperties(resource, properties) ? properties : null;
	}

	public static boolean getProperties(ResourceLocation resource, Properties properties) {
		return resource != null && instance.getPropertiesImpl(resource, properties);
	}

	public static ResourceLocation transformResourceLocation(ResourceLocation resource, String oldExt, String newExt) {
		return new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath().replaceFirst(Pattern.quote(oldExt) + "$", newExt));
	}

	public static ResourceLocation parseResourceLocation(ResourceLocation baseResource, String path) {
		if (path != null && !path.equals("")) {
			boolean absolute = false;

			if (path.startsWith("%blur%")) {
				path = path.substring(6);
			}

			if (path.startsWith("%clamp%")) {
				path = path.substring(7);
			}

			if (path.startsWith("/")) {
				path = path.substring(1);
				absolute = true;
			}

			if (path.startsWith("assets/minecraft/")) {
				path = path.substring(17);
				absolute = true;
			}

			int colon = path.indexOf(58);
			return colon >= 0 ? new ResourceLocation(path.substring(0, colon), path.substring(colon + 1)) : (path.startsWith("~/") ? new ResourceLocation(baseResource.getResourceDomain(), "mcpatcher/" + path.substring(2)) : (path.startsWith("./") ? new ResourceLocation(baseResource.getResourceDomain(), baseResource.getResourcePath().replaceFirst("[^/]+$", "") + path.substring(2)) : (!absolute && !path.contains("/") ? new ResourceLocation(baseResource.getResourceDomain(), baseResource.getResourcePath().replaceFirst("[^/]+$", "") + path) : new ResourceLocation(baseResource.getResourceDomain(), path))));
		} else {
			return null;
		}
	}

	public static ResourceLocation newMCPatcherResourceLocation(String path) {
		return new ResourceLocation("mcpatcher/" + path);
	}

	public static List<ResourceLocation> listResources(String directory, String suffix, boolean recursive, boolean directories, boolean sortByFilename) {
		return listResources((String)null, directory, suffix, recursive, directories, sortByFilename);
	}

	public static List<ResourceLocation> listResources(String namespace, String directory, String suffix, boolean recursive, boolean directories, boolean sortByFilename) {
		if (suffix == null) {
			suffix = "";
		}

		ArrayList resources = new ArrayList();

		if (MCPatcherUtils.isNullOrEmpty(namespace)) {
			Iterator i$ = getNamespaces().iterator();

			while (i$.hasNext()) {
				String namespace1 = (String)i$.next();
				findResources(namespace1, directory, suffix, recursive, directories, resources);
			}
		} else {
			findResources(namespace, directory, suffix, recursive, directories, resources);
		}

		Collections.sort(resources, new TexturePackAPI$1(sortByFilename));
		return resources;
	}

	private static void findResources(String namespace, String directory, String suffix, boolean recursive, boolean directories, Collection<ResourceLocation> resources) {
		Iterator i$ = getResourcePacks(namespace).iterator();

		while (i$.hasNext()) {
			ResourcePack resourcePack = (ResourcePack)i$.next();

			if (resourcePack instanceof FileResourcePack) {
				ZipFile base = ((FileResourcePack)resourcePack).resourcePackZipFile;

				if (base != null) {
					findResources(base, namespace, "assets/" + namespace, directory, suffix, recursive, directories, resources);
				}
			} else {
				File base1;

				if (resourcePack instanceof DefaultResourcePack) {
					if ("minecraft".equals(namespace)) {
						base1 = ((DefaultResourcePack)resourcePack).fileAssets;

						if (base1 != null && base1.isDirectory()) {
							findResources(base1, namespace, directory, suffix, recursive, directories, resources);
						}
					}
				} else if (resourcePack instanceof AbstractResourcePack) {
					base1 = ((AbstractResourcePack)resourcePack).resourcePackFile;

					if (base1 != null && base1.isDirectory()) {
						base1 = new File(base1, "assets/" + namespace);

						if (base1.isDirectory()) {
							findResources(base1, namespace, directory, suffix, recursive, directories, resources);
						}
					}
				}
			}
		}
	}

	private static void findResources(ZipFile zipFile, String namespace, String root, String directory, String suffix, boolean recursive, boolean directories, Collection<ResourceLocation> resources) {
		String base = root + "/" + directory;
		Iterator i$ = Collections.list(zipFile.entries()).iterator();

		while (i$.hasNext()) {
			ZipEntry entry = (ZipEntry)i$.next();

			if (entry.isDirectory() == directories) {
				String name = entry.getName().replaceFirst("^/", "");

				if (name.startsWith(base) && name.endsWith(suffix)) {
					if (directory.equals("")) {
						if (recursive || !name.contains("/")) {
							resources.add(new ResourceLocation(namespace, name));
						}
					} else {
						String subpath = name.substring(base.length());

						if ((subpath.equals("") || subpath.startsWith("/")) && (recursive || subpath.equals("") || !subpath.substring(1).contains("/"))) {
							resources.add(new ResourceLocation(namespace, name.substring(root.length() + 1)));
						}
					}
				}
			}
		}
	}

	private static void findResources(File base, String namespace, String directory, String suffix, boolean recursive, boolean directories, Collection<ResourceLocation> resources) {
		File subdirectory = new File(base, directory);
		String[] list = subdirectory.list();

		if (list != null) {
			String pathComponent = directory.equals("") ? "" : directory + "/";
			String[] arr$ = list;
			int len$ = list.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				String s = arr$[i$];
				File entry = new File(subdirectory, s);
				String resourceName = pathComponent + s;

				if (entry.isDirectory()) {
					if (directories && s.endsWith(suffix)) {
						resources.add(new ResourceLocation(namespace, resourceName));
					}

					if (recursive) {
						findResources(base, namespace, pathComponent + s, suffix, recursive, directories, resources);
					}
				} else if (s.endsWith(suffix) && !directories) {
					resources.add(new ResourceLocation(namespace, resourceName));
				}
			}
		}
	}

	public static int getTextureIfLoaded(ResourceLocation resource) {
		if (resource == null) {
			return -1;
		} else {
			TextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(resource);
			return texture instanceof AbstractTexture ? ((AbstractTexture)texture).glTextureId : -1;
		}
	}

	public static boolean isTextureLoaded(ResourceLocation resource) {
		return getTextureIfLoaded(resource) >= 0;
	}

	public static TextureObject getTextureObject(ResourceLocation resource) {
		return Minecraft.getMinecraft().getTextureManager().getTexture(resource);
	}

	public static void bindTexture(ResourceLocation resource) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
	}

	public static void bindTexture(int texture) {
		if (texture >= 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		}
	}

	public static void unloadTexture(ResourceLocation resource) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		TextureObject texture = textureManager.getTexture(resource);

		if (texture != null && !(texture instanceof TextureMap) && !(texture instanceof DynamicTexture)) {
			if (texture instanceof AbstractTexture) {
				((AbstractTexture)texture).unloadGLTexture();
			}

			logger.finer("unloading texture %s", new Object[] {resource});
			textureManager.mapTextureObjects.remove(resource);
		}
	}

	public static void deleteTexture(int texture) {
		if (texture >= 0) {
			GL11.glDeleteTextures(texture);
		}
	}

	protected InputStream getInputStreamImpl(ResourceLocation resource) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(resource).getInputStream();
		} catch (IOException var3) {
			return null;
		}
	}

	protected BufferedImage getImageImpl(ResourceLocation resource) {
		InputStream input = getInputStream(resource);
		BufferedImage image = null;

		if (input != null) {
			try {
				image = ImageIO.read(input);
			} catch (IOException var8) {
				logger.error("could not read %s", new Object[] {resource});
				var8.printStackTrace();
			} finally {
				MCPatcherUtils.close((Closeable)input);
			}
		}

		return image;
	}

	protected boolean getPropertiesImpl(ResourceLocation resource, Properties properties) {
		if (properties != null) {
			InputStream input = getInputStream(resource);
			boolean e;

			try {
				if (input == null) {
					return false;
				}

				properties.load(input);
				e = true;
			} catch (IOException var8) {
				logger.error("could not read %s", new Object[] {resource});
				var8.printStackTrace();
				return false;
			} finally {
				MCPatcherUtils.close((Closeable)input);
			}

			return e;
		} else {
			return false;
		}
	}
}
