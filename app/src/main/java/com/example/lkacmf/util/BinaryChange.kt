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