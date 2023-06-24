package com.pisces.framework.core.utils.io;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.utils.io.visitor.DelVisitor;
import com.pisces.framework.core.utils.io.visitor.MoveVisitor;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件跑龙套
 *
 * @author jason
 * @date 2022/12/07
 */
public final class FileUtils {

    protected FileUtils() {
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            final String[] subPaths = file.list();
            if (subPaths != null) {
                for (String subPath : subPaths) {
                    deleteFile(path + File.separator + subPath);
                }
            }
        }

        return file.delete();
    }

    public static String findJarPath() {
        String filePath = FileUtils.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile();

        try {
            filePath = java.net.URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Path path = null;
        if (!filePath.contains(".jar")) {
            File f = new File(filePath);
            path = Paths.get(f.toURI());
        } else {
            try {
                path = Paths.get(new URI(filePath));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (!filePath.contains(".jar")) {
            return path != null ? path.getParent().toString() : "";
        }

        String tempPath = filePath.substring(0, filePath.indexOf(".jar"));
        if (tempPath.startsWith("file:")) {
            tempPath = tempPath.substring(5, tempPath.lastIndexOf("/"));
        } else {
            tempPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
        }
        return tempPath;
    }

    public static boolean checkFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String readFileContent(String filePath) {
        if (!checkFileExist(filePath)) {
            return "文件不存在:" + filePath;
        }
        StringBuilder stringBuffer = new StringBuilder();
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuffer.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static void writeFile(String filePath, String content) {
        if (!checkFileExist(filePath)) {
            try {
                File file = new File(filePath);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePath, String content, boolean append) {
        if (!checkFileExist(filePath)) {
            try {
                File file = new File(filePath);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(filePath, append)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Class<?>> loadClass(String packName) {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String strFile = packName.replaceAll("\\.", "/");
        try {
            Enumeration<URL> urls = loader.getResources(strFile);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    String filePath = url.getPath();
                    if ("file".equals(protocol)) {
                        loadClassImpl(packName, filePath, classes);
                    } else if ("jar".equals(protocol)) {
                        loadJarImpl(packName, url, classes);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void loadClassImpl(String packName, String dirPath, List<Class<?>> clses) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirFiles = dir.listFiles((File file) -> file.isDirectory() || file.getName().endsWith(".class"));
        if (dirFiles == null) {
            return;
        }
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                loadClassImpl(packName + "." + file.getName(), dirPath + "/" + file.getName(), clses);
            } else {
                String clsName = file.getName();
                clsName = clsName.substring(0, clsName.length() - 6);
                try {
                    clses.add(Class.forName(packName + "." + clsName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadJarImpl(String packName, URL url, List<Class<?>> clses) throws IOException {
        JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarUrlConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            final String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(".class")) {
                String clsName = jarEntryName.replace("/", ".");
                if (clsName.startsWith(packName)) {
                    clsName = clsName.substring(0, clsName.length() - 6);
                    try {
                        clses.add(Class.forName(clsName));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param fullFilePath 文件的全路径，使用POSIX风格
     * @return 文件，若路径为null，返回null
     */
    public static File touch(String fullFilePath) {
        if (fullFilePath == null) {
            return null;
        }
        return touch(new File(fullFilePath));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     */
    public static File touch(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkdir(file.getParentFile());
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return file;
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     * @since 5.5.3
     */
    public static boolean exists(Path path, boolean isFollowLinks) {
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.exists(path, options);
    }

    /**
     * 创建文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = new File(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建所给目录及其父目录
     *
     * @param dir 目录
     * @return 目录
     * @since 5.5.7
     */
    public static Path mkdir(Path dir) {
        if (null != dir && !exists(dir, false)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new SystemException(e);
            }
        }
        return dir;
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir 临时文件创建的所在目录
     * @return 临时文件
     */
    public static File createTempFile(File dir) {
        return createTempFile("hutool", null, dir, true);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     */
    public static File createTempFile(File dir, boolean isReCreat) {
        return createTempFile("hutool", null, dir, isReCreat);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].suffix From com.jodd.io.FileUtil
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     */
    public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) {
        int exceptionsCount = 0;
        while (true) {
            try {
                File file = File.createTempFile(prefix, suffix, mkdir(dir)).getCanonicalFile();
                if (isReCreat) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                return file;
            } catch (IOException ioex) { // fixes java.io.WinNTFileSystem.createFileExclusively access denied
                if (++exceptionsCount >= 50) {
                    throw new SystemException(ioex);
                }
            }
        }
    }

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     * @since 3.0.9
     */
    public static boolean isWindows() {
        return FileNameUtil.WINDOWS_SEPARATOR == File.separatorChar;
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指Path对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @since 5.4.1
     */
    public static boolean equals(Path file1, Path file2) {
        try {
            return Files.isSameFile(file1, file2);
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     */
    public static boolean equals(File file1, File file2) {
        if (!file1.exists() || !file2.exists()) {
            // 两个文件都不存在判断其路径是否相同， 对于一个存在一个不存在的情况，一定不相同
            return !file1.exists() && !file2.exists() && pathEquals(file1, file2);
        }
        return equals(file1.toPath(), file2.toPath());
    }

    /**
     * 文件路径是否相同<br>
     * 取两个文件的绝对路径比较，在Windows下忽略大小写，在Linux下不忽略。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件路径是否相同
     * @since 3.0.9
     */
    public static boolean pathEquals(File file1, File file2) {
        if (isWindows()) {
            // Windows环境
            try {
                if (file1.getCanonicalPath().equalsIgnoreCase(file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (file1.getAbsolutePath().equalsIgnoreCase(file2.getAbsolutePath())) {
                    return true;
                }
            }
        } else {
            // 类Unix环境
            try {
                if (file1.getCanonicalPath().equals(file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 复制文件或目录<br>
     * 如果目标文件为目录，则将源文件以相同文件名拷贝到目标目录
     *
     * @param srcPath    源文件或目录
     * @param destPath   目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     */
    public static File copy(String srcPath, String destPath, boolean isOverride) {
        return copy(new File(srcPath), new File(destPath), isOverride);
    }

    /**
     * 复制文件或目录<br>
     * 情况如下：
     *
     * <pre>
     * 1、src和dest都为目录，则将src目录及其目录下所有文件目录拷贝到dest下
     * 2、src和dest都为文件，直接复制，名字为dest
     * 3、src为文件，dest为目录，将src拷贝到dest目录下
     * </pre>
     *
     * @param src        源文件
     * @param dest       目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     */
    public static File copy(File src, File dest, boolean isOverride) {
        return FileCopier.create(src, dest).setOverride(isOverride).copy();
    }

    /**
     * 复制文件或目录<br>
     * 情况如下：
     *
     * <pre>
     * 1、src和dest都为目录，则讲src下所有文件目录拷贝到dest下
     * 2、src和dest都为文件，直接复制，名字为dest
     * 3、src为文件，dest为目录，将src拷贝到dest目录下
     * </pre>
     *
     * @param src        源文件
     * @param dest       目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     */
    public static File copyContent(File src, File dest, boolean isOverride) {
        return FileCopier.create(src, dest).setCopyContentIfDir(true).setOverride(isOverride).copy();
    }

    /**
     * 复制文件或目录<br>
     * 情况如下：
     *
     * <pre>
     * 1、src和dest都为目录，则讲src下所有文件（包括子目录）拷贝到dest下
     * 2、src和dest都为文件，直接复制，名字为dest
     * 3、src为文件，dest为目录，将src拷贝到dest目录下
     * </pre>
     *
     * @param src        源文件
     * @param dest       目标文件或目录，目标不存在会自动创建（目录、文件都创建）
     * @param isOverride 是否覆盖目标文件
     * @return 目标目录或文件
     * @since 4.1.5
     */
    public static File copyFilesFromDir(File src, File dest, boolean isOverride) {
        return FileCopier.create(src, dest).setCopyContentIfDir(true).setOnlyCopyFile(true).setOverride(isOverride).copy();
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     *
     * @param path {@link Path}
     * @return 如果为目录true
     * @since 5.5.1
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param path          {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     * @since 3.1.0
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isDirectory(path, options);
    }

    /**
     * 移动文件或目录<br>
     * 当目标是目录时，会将源文件或文件夹整体移动至目标目录下<br>
     * 例如：
     * <ul>
     *     <li>move("/usr/aaa/abc.txt", "/usr/bbb")结果为："/usr/bbb/abc.txt"</li>
     *     <li>move("/usr/aaa", "/usr/bbb")结果为："/usr/bbb/aaa"</li>
     * </ul>
     *
     * @param src        源文件或目录路径
     * @param target     目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.5.1
     */
    public static Path move(Path src, Path target, boolean isOverride) {
        if (isDirectory(target)) {
            target = target.resolve(src.getFileName());
        }
        return moveContent(src, target, isOverride);
    }

    /**
     * 移动文件或目录内容到目标目录中，例如：
     * <ul>
     *     <li>moveContent("/usr/aaa/abc.txt", "/usr/bbb")结果为："/usr/bbb/abc.txt"</li>
     *     <li>moveContent("/usr/aaa", "/usr/bbb")结果为："/usr/bbb"</li>
     * </ul>
     *
     * @param src        源文件或目录路径
     * @param target     目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.7.9
     */
    public static Path moveContent(Path src, Path target, boolean isOverride) {
        final CopyOption[] options = isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{};

        // 自动创建目标的父目录
        mkdir(target.getParent());
        try {
            return Files.move(src, target, options);
        } catch (IOException e) {
            // 移动失败，可能是跨分区移动导致的，采用递归移动方式
            try {
                Files.walkFileTree(src, new MoveVisitor(src, target, options));
                // 移动后空目录没有删除，
                del(src);
            } catch (IOException e2) {
                throw new SystemException(e2);
            }
            return target;
        }
    }

    /**
     * 移动文件或者目录
     *
     * @param src        源文件或者目录
     * @param target     目标文件或者目录
     * @param isOverride 是否覆盖目标，只有目标为文件才覆盖
     */
    public static void move(File src, File target, boolean isOverride) {
        move(src.toPath(), target.toPath(), isOverride);
    }

    /**
     * 移动文件或者目录
     *
     * @param src        源文件或者目录
     * @param target     目标文件或者目录
     * @param isOverride 是否覆盖目标，只有目标为文件才覆盖
     * @since 5.7.9
     */
    public static void moveContent(File src, File target, boolean isOverride) {
        moveContent(src.toPath(), target.toPath(), isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名，不保留扩展名。<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.png", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * @param file       被修改的文件
     * @param newName    新的文件名，如需扩展名，需自行在此参数加上，原文件名的扩展名不会被保留
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     * @since 5.3.6
     */
    public static File rename(File file, String newName, boolean isOverride) {
        return rename(file, newName, false, isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     * 重命名有两种模式：<br>
     * 1、isRetainExt为true时，保留原扩展名：
     *
     * <pre>
     * FileUtil.rename(file, "aaa", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * <p>
     * 2、isRetainExt为false时，不保留原扩展名，需要在newName中
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param file        被修改的文件
     * @param newName     新的文件名，包括扩展名
     * @param isRetainExt 是否保留原文件的扩展名，如果保留，则newName不需要加扩展名
     * @param isOverride  是否覆盖目标文件
     * @return 目标文件
     * @since 3.0.9
     */
    public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
        if (isRetainExt) {
            final String extName = FileNameUtil.extName(file);
            if (StringUtils.isNotBlank(extName)) {
                newName = newName.concat(".").concat(extName);
            }
        }
        return rename(file.toPath(), newName, isOverride).toFile();
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param path       被修改的文件
     * @param newName    新的文件名，包括扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.4.1
     */
    public static Path rename(Path path, String newName, boolean isOverride) {
        return move(path, path.resolveSibling(newName), isOverride);
    }

    /**
     * 删除文件或者文件夹，不追踪软链<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param path 文件对象
     * @return 成功与否
     * @since 4.4.2
     */
    public static boolean del(Path path) {
        if (Files.notExists(path)) {
            return true;
        }

        try {
            if (isDirectory(path)) {
                Files.walkFileTree(path, DelVisitor.INSTANCE);
            } else {
                delFile(path);
            }
        } catch (IOException e) {
            throw new SystemException(e);
        }
        return true;
    }

    /**
     * 删除文件或空目录，不追踪软链
     *
     * @param path 文件对象
     * @throws IOException IO异常
     * @since 5.7.7
     */
    protected static void delFile(Path path) throws IOException {
        try {
            Files.delete(path);
        } catch (AccessDeniedException e) {
            // 可能遇到只读文件，无法删除.使用 file 方法删除
            if (!path.toFile().delete()) {
                throw e;
            }
        }
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub    子目录
     * @return 子目录是否为父目录的子目录
     * @since 5.5.5
     */
    public static boolean isSub(Path parent, Path sub) {
        return toAbsNormal(sub).startsWith(toAbsNormal(parent));
    }

    /**
     * 将Path路径转换为标准的绝对路径
     *
     * @param path 文件或目录Path
     * @return 转换后的Path
     * @since 5.5.5
     */
    public static Path toAbsNormal(Path path) {
        return path.toAbsolutePath().normalize();
    }
}
