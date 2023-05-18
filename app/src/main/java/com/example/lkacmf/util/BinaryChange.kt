package com.example.lkacmf.util

class BinaryChange {
    fun tenToHex(data:Int) = Integer.toHexString(data)!!

    /**
     * hexString 转byte
     */
    fun hexStringToByte(data:String): Array<String> {
        check(data.length % 2 == 0) { "Must have an even length" }
        val byteIterator = data.chunkedSequence(2)
            .map {it}
            .iterator()
        return Array(data.length / 2) { byteIterator.next() }
    }

}

//十进制转2进制13->1101
//13.toString(2)
// 或者
//Integer.toBinaryString(13)

//十进制转8进制 13->15
//13.toString(8)
// 或者
//Integer.toOctalString(13)

//十进制转16进制 13->d
//13.toString(16)
// 或者
//Integer.toHexString(13)

//2进制转8进制 1101->13->15
//kotlin复制代码// 先转10进制再转8进制
//"1101".toInt(2).toString(8)
//2进制转10进制 1101->13
//kotlin复制代码"1101".toInt(2)
// 或者
//Integer.valueOf("1101",2)

//2进制转16进制 1101->13->d
// 先转10进制再转16进制
//"1101".toInt(2).toString(16)

//八进制
//8进制转2进制 15->13->1101
//kotlin复制代码// 先转10进制再转2进制
//"15".toInt(8).toString(2)
//8进制转10进制 15->13
//kotlin复制代码"15".toInt(8)
// 或者
//Integer.valueOf("15",8)
//8进制转16进制 15->13->d
//kotlin复制代码// 先转10进制再转16进制
//"15".toInt(8).toString(16)

//十六进制
//16进制转2进制 d->13->1101
//kotlin复制代码// 先转10进制再转2进制
//"d".toInt(16).toString(2)
//16进制转8进制 d->13->15
//kotlin复制代码// 先转10进制再转8进制
//"d".toInt(16).toString(8)
//16进制转10进制 d->13
//kotlin复制代码"d".toInt(16)
//或者
//Integer.valueOf("d",16)