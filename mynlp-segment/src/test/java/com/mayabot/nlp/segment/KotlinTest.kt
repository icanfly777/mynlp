package com.mayabot.nlp.segment

import com.mayabot.nlp.Mynlps
import com.mayabot.nlp.segment.kotlin.lexer
import com.mayabot.nlp.segment.kotlin.segment
import com.mayabot.nlp.segment.lexer.core.CoreDictionary


fun main() {
    println("录音曝光！朴槿惠就职总统前 听崔顺实90分钟指导".lexer())
    println("录音曝光！朴槿惠就职总统前 听崔顺实90分钟指导".segment())
}