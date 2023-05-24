package com.example.lkacmf.util.pio

import org.apache.poi.xwpf.usermodel.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


object XwpfTUtil {
    /**
     * 替换段落里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    fun replaceInPara(doc: XWPFDocument, params: Map<String, Any>) {
        val iterator = doc.paragraphsIterator
        var para: XWPFParagraph
        while (iterator.hasNext()) {
            para = iterator.next()
            replaceInPara(para, params)
        }
    }

    /**
     * 替换段落里面的变量
     *
     * @param para   要替换的段落
     * @param params 参数
     */
    fun replaceInPara(para: XWPFParagraph, params: Map<String, Any>) {
        val runs: List<XWPFRun>
        var matcher: Matcher
        var runText: String? = ""
        if (matcher(para.paragraphText).find()) {
            runs = para.runs
            if (runs.size > 0) {
                val j = runs.size
                for (i in 0 until j) {
                    val run = runs[0]
                    val i1 = run.toString()
                    runText += i1
                    para.removeRun(0)
                }
            }
            matcher = matcher(runText!!)
            if (matcher.find()) {
                while (matcher(runText!!).also { matcher = it }.find()) {
                    runText =
                        matcher.replaceFirst(java.lang.String.valueOf(params[matcher.group(1)]))
                }
                //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                para.insertNewRun(0).setText(runText)
            }
        }

    }

    /**
     * 替换表格里面的变量
     *
     * @param doc    要替换的文档
     * @param params 参数
     */
    fun replaceInTable(doc: XWPFDocument, params: Map<String, Any>) {
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

    /**
     * 正则匹配字符串
     *
     * @param str
     * @return
     */
    private fun matcher(str: String): Matcher {
        val pattern = Pattern.compile(
            "\\$\\{(.+?)\\}",
            Pattern.CASE_INSENSITIVE
        )
        return pattern.matcher(str)
    }

    /**
     * 关闭输入流
     *
     * @param is
     */
    fun close(`is`: InputStream?) {
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
     *
     * @param os
     */
    fun close(os: OutputStream?) {
        if (os != null) {
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}