package com.example.lkacmf.util.pio

import android.os.Environment
import org.apache.poi.xwpf.usermodel.*
import java.io.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class XwpfTest {
    /**
     * 用一个docx文档作为模板，然后替换其中的内容，再写入目标文档中。
     * @throws Exception
     */
    @Throws(Exception::class)
    fun testTemplateWrite(open: InputStream) {
        val params: MutableMap<String, Any> = HashMap()
        params["date"] = "2014-02-28"
        val doc = XWPFDocument(open)
        //替换段落里面的变量
//        this.replaceInPara(doc, params)
        //替换表格里面的变量
        replaceInTable(doc, params)
        val os = FileOutputStream(
            Environment.getExternalStorageDirectory().path + "/acmf1.docx")
        doc.write(os)
        this.close(os)
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
            this.replaceInPara(para, params)
        }
    }

    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private fun replaceInPara(para: XWPFParagraph, params: Map<String, Any>) {
        val runs: List<XWPFRun>
        var matcher: Matcher
        if (matcher(para.paragraphText).find()) {
            runs = para.runs
            for (i in runs.indices) {
                val run = runs[i]
                var runText = run.toString()
                matcher = matcher(runText)
                if (matcher.find()) {
                    while (matcher(runText).also { matcher = it }.find()) {
                        runText = matcher.replaceFirst(params[matcher.group(1)].toString())
                    }
                    //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                    //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                    para.removeRun(i)
                    para.insertNewRun(i).setText(runText)
                }
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
                        this.replaceInPara(para, params)
                    }
                }
            }
        }

    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    private fun matcher(str: String): Matcher {
        val pattern: Pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE)
        return pattern.matcher(str)
    }

    /**
     * 关闭输入流
     * @param is
     */
    private fun close(`is`: InputStream?) {
        if (`is` != null) {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    private fun close(os: OutputStream?) {
        if (os != null) {
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}