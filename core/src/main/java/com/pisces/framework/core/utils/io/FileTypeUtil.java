package com.pisces.framework.core.utils.io;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.utils.lang.HexUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 文件类型判断工具类
 *
 * <p>此工具根据文件的前几位bytes猜测文件类型，对于文本、zip判断不准确，对于视频、图片类型判断准确</p>
 *
 * <p>需要注意的是，xlsx、docx等Office2007格式，全部识别为zip，因为新版采用了OpenXML格式，这些格式本质上是XML文件打包为zip</p>
 *
 * @author Looly
 */
public class FileTypeUtil {

    private static final Map<String, String> FILE_TYPE_MAP;

    static {
        FILE_TYPE_MAP = new ConcurrentSkipListMap<>((s1, s2) -> {
            int len1 = s1.length();
            int len2 = s2.length();
            if (len1 == len2) {
                return s1.compareTo(s2);
            } else {
                return len2 - len1;
            }
        });

        // JPEG (jpg)
        FILE_TYPE_MAP.put("ffd8ff", "jpg");
        // PNG (png)
        FILE_TYPE_MAP.put("89504e47", "png");
        // GIF (gif)
        FILE_TYPE_MAP.put("4749463837", "gif");
        // GIF (gif)
        FILE_TYPE_MAP.put("4749463839", "gif");
        // TIFF (tif)
        FILE_TYPE_MAP.put("49492a00227105008037", "tif");
        // 16色位图(bmp)
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp");
        // 24色位图(bmp)
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp");
        // 256色位图(bmp)
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp");
        // CAD (dwg)
        FILE_TYPE_MAP.put("41433130313500000000", "dwg");
        // Rich Text Format (rtf)
        FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf");
        // Photoshop (psd)
        FILE_TYPE_MAP.put("38425053000100000000", "psd");
        // Email [Outlook Express 6] (eml)
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml");
        // MS Access (mdb)
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
        FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
        // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("255044462d312e", "pdf");
        // rmvb/rm相同
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb");
        // flv与f4v相同
        FILE_TYPE_MAP.put("464c5601050000000900", "flv");
        FILE_TYPE_MAP.put("0000001C66747970", "mp4");
        FILE_TYPE_MAP.put("00000020667479706", "mp4");
        FILE_TYPE_MAP.put("00000018667479706D70", "mp4");
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg");
        // wmv与asf相同
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv");
        // Wave (wav)
        FILE_TYPE_MAP.put("52494646e27807005741", "wav");
        FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
        // MIDI (mid)
        FILE_TYPE_MAP.put("4d546864000000060001", "mid");
        // WinRAR
        FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
        FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
        FILE_TYPE_MAP.put("504B03040a0000000000", "jar");
        FILE_TYPE_MAP.put("504B0304140008000800", "jar");
        // MS Excel 注意：word、msi 和 excel的文件头一样
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10", "xls");
        FILE_TYPE_MAP.put("504B0304", "zip");
        // 可执行文件
        FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");
        // jsp文件
        FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");
        // MF文件
        FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");
        // java文件
        FILE_TYPE_MAP.put("7061636b616765207765", "java");
        // bat文件
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat");
        // gz文件
        FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");
        // class文件
        FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");
        // chm文件
        FILE_TYPE_MAP.put("49545346030000006000", "chm");
        // mxp文件
        FILE_TYPE_MAP.put("04000000010000001300", "mxp");
        FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
        // Quicktime (mov)
        FILE_TYPE_MAP.put("6D6F6F76", "mov");
        // WordPerfect (wpd)
        FILE_TYPE_MAP.put("FF575043", "wpd");
        // Outlook Express (dbx)
        FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx");
        // Outlook (pst)
        FILE_TYPE_MAP.put("2142444E", "pst");
        // Quicken (qdf)
        FILE_TYPE_MAP.put("AC9EBD8F", "qdf");
        // Windows Password (pwl)
        FILE_TYPE_MAP.put("E3828596", "pwl");
        // Real Audio (ram)
        FILE_TYPE_MAP.put("2E7261FD", "ram");
    }

    /**
     * 增加文件类型映射<br>
     * 如果已经存在将覆盖之前的映射
     *
     * @param fileStreamHexHead 文件流头部Hex信息
     * @param extName           文件扩展名
     * @return 之前已经存在的文件扩展名
     */
    public static String putFileType(String fileStreamHexHead, String extName) {
        return FILE_TYPE_MAP.put(fileStreamHexHead, extName);
    }

    /**
     * 移除文件类型映射
     *
     * @param fileStreamHexHead 文件流头部Hex信息
     * @return 移除的文件扩展名
     */
    public static String removeFileType(String fileStreamHexHead) {
        return FILE_TYPE_MAP.remove(fileStreamHexHead);
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * @param fileStreamHexHead 文件流头部16进制字符串
     * @return 文件类型，未找到为{@code null}
     */
    public static String getType(String fileStreamHexHead) {
        for (Entry<String, String> fileTypeEntry : FILE_TYPE_MAP.entrySet()) {
            if (fileStreamHexHead.startsWith(fileTypeEntry.getKey())) {
                return fileTypeEntry.getValue();
            }
        }
        return null;
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * @param in {@link InputStream}
     * @return 类型，文件的扩展名，未找到为{@code null}
     */
    public static String getType(InputStream in) {
        return getType(readHex28Upper(in));
    }

    /**
     * 读取16进制字符串
     *
     * @param in          {@link InputStream}
     * @param length      长度
     * @param toLowerCase true 传换成小写格式 ， false 传换成大写格式
     * @return 16进制字符串
     */
    public static String readHex(InputStream in, int length, boolean toLowerCase) {
        return HexUtil.encodeHexStr(readBytes(in, length), toLowerCase);
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为null返回null
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     */
    public static byte[] readBytes(InputStream in, int length) {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try {
            readLength = in.read(b);
        } catch (IOException e) {
            throw new SystemException(e);
        }
        if (readLength > 0 && readLength < length) {
            byte[] b2 = new byte[readLength];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        } else {
            return b;
        }
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用大写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     */
    public static String readHex28Upper(InputStream in) {
        return readHex(in, 28, false);
    }


    /**
     * 根据文件流的头部信息获得文件类型
     *
     * <pre>
     *     1、无法识别类型默认按照扩展名识别
     *     2、xls、doc、msi头信息无法区分，按照扩展名区分
     *     3、zip可能为docx、xlsx、pptx、jar、war、ofd头信息无法区分，按照扩展名区分
     * </pre>
     *
     * @param in       {@link InputStream}
     * @param filename 文件名
     * @return 类型，文件的扩展名，未找到为{@code null}
     */
    public static String getType(InputStream in, String filename) {
        String typeName = getType(in);

        if (null == typeName) {
            // 未成功识别类型，扩展名辅助识别
            typeName = FileNameUtil.extName(filename);
        } else if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            final String extName = FileNameUtil.extName(filename);
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war、ofd等格式，扩展名辅助判断
            final String extName = FileNameUtil.extName(filename);
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            } else if ("ofd".equalsIgnoreCase(extName)) {
                typeName = "ofd";
            }
        } else if ("jar".equals(typeName)) {
            // wps编辑过的.xlsx文件与.jar的开头相同,通过扩展名判断
            final String extName = FileNameUtil.extName(filename);
            if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("docx".equalsIgnoreCase(extName)) {
                // issue#I47JGH
                typeName = "docx";
            }
        }
        return typeName;
    }

    /**
     * 根据文件流的头部信息获得文件类型
     *
     * <pre>
     *     1、无法识别类型默认按照扩展名识别
     *     2、xls、doc、msi头信息无法区分，按照扩展名区分
     *     3、zip可能为docx、xlsx、pptx、jar、war头信息无法区分，按照扩展名区分
     * </pre>
     *
     * @param file 文件 {@link File}
     * @return 类型，文件的扩展名，未找到为{@code null}
     */
    public static String getType(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            return getType(in, file.getName());
        } catch (IOException ignored) {
        }
        return "";
    }
}
