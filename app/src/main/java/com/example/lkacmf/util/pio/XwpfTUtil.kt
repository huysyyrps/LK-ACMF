package com.example.lkacmf.util.pio

import android.graphics.Bitmap
import android.os.Environment
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.util.BitmapSave
import com.example.lkacmf.util.Constant
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object XwpfTUtil {
    /**
     * 生成一个docx文件，主要用于直接读取asset目录下的模板文件，不用先复制到sd卡中
     * @param templetDocInStream  模板文件的InputStream
     * @param targetDocPath 生成的目标文件的完整路径
     * @param dataMap 替换的数据
     */
    fun writeDocx(
        activity: MainActivity,
        templetDocInStream: InputStream,
        dataMap: MutableMap<String, Any>,
        bitmapBX: Bitmap,
        bitmapBZ: Bitmap,
        bitmapDX: Bitmap
    ):Boolean {
        try {
            //得到模板doc文件的HWPFDocument对象
            val HDocx = XWPFDocument(templetDocInStream)
            //替换段落里面的变量
            replaceInPara(HDocx, dataMap)
            //替换表格里面的变量
            replaceInTable(HDocx, dataMap)


            val run: XWPFRun = HDocx.createParagraph().createRun()
//            val run1: XWPFRun = HDocx.createParagraph().createRun()
//            val picIn = FileInputStream(File(Environment.getExternalStorageDirectory().path + "/123.png"))
            val baosBX = ByteArrayOutputStream()
            bitmapBX.compress(Bitmap.CompressFormat.PNG, 100, baosBX)
            val picInBX = ByteArrayInputStream(baosBX.toByteArray())

            val baosBZ = ByteArrayOutputStream()
            bitmapBZ.compress(Bitmap.CompressFormat.PNG, 100, baosBZ)
            val picInBZ = ByteArrayInputStream(baosBZ.toByteArray())

            val baosDX = ByteArrayOutputStream()
            bitmapDX.compress(Bitmap.CompressFormat.PNG, 100, baosDX)
            val picInDX = ByteArrayInputStream(baosDX.toByteArray())

            run.addPicture(
                picInBX,
                XWPFDocument.PICTURE_TYPE_PNG,
                "插入图片",
                Units.toEMU(205.0),
                Units.toEMU(130.0)
            )
            run.addPicture(
                picInBZ,
                XWPFDocument.PICTURE_TYPE_PNG,
                "插入图片1",
                Units.toEMU(205.0),
                Units.toEMU(130.0)
            )
            run.addPicture(
                picInDX,
                XWPFDocument.PICTURE_TYPE_PNG,
                "插入图片1",
                Units.toEMU(205.0),
                Units.toEMU(160.0)
            )
            picInBX.close()
            picInBZ.close()
            picInDX.close()


            val targetDocPath = activity.externalCacheDir.toString()+ "/"+Constant.SAVE_FORM_PATH+"/"
            val file = File(targetDocPath)
            //如果不存在  就mkdirs()创建此文件夹
            if (!file.exists()) {
                file.mkdirs()
            }
            val mFile = File(targetDocPath + getNowDate())
            //写到另一个文件中
            val os: OutputStream = FileOutputStream(mFile)
            //把doc输出到输出流中
            HDocx.write(os)
            os.close()
            templetDocInStream.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
    /**
     * 获取当前时间,用来给文件夹命名
     */
    private fun getNowDate(): String? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return format.format(Date()) + ".docx"
    }

    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private fun replaceInPara(doc: XWPFDocument, params: Map<String, Any>) {
        val iterator = doc.paragraphsIterator
        var para: XWPFParagraph
        while (iterator.hasNext()) {
            para = iterator.next()
            replaceInPara(para, params)
        }
    }


    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private fun replaceInPara(para: XWPFParagraph, params: Map<String, Any>) {
        val runs: List<XWPFRun>
        println("para.getParagraphText()==" + para.paragraphText)
        runs = para.runs
        for (i in runs.indices) {
            val run = runs[i]
            var runText = run.toString()
            println("runText==$runText")

            // 替换文本内容，将自定义的$xxx$替换成实际文本
            for ((key, value) in params) {
                runText = runText.replace(key, value.toString() + "")
                //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                para.removeRun(i)
                para.insertNewRun(i).setText(runText)
            }
        }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private fun replaceInTable(doc: XWPFDocument, params: Map<String, Any>) {
        val iterator = doc.tablesIterator
        var table: XWPFTable
        var rows: List<XWPFTableRow>
        var cells: List<XWPFTableCell>
        var paras: List<XWPFParagraph>
        while (iterator.hasNext()) {
            table = iterator.next()
            rows = table.rows
            for (row in rows) {
                cells = row.tableCells
                for (cell in cells) {
                    paras = cell.paragraphs
                    for (para in paras) {
                        replaceInPara(para, params)
                    }
                }
            }
        }
    }
}