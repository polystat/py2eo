package org.polystat.py2eo.transpiler

import org.junit.Test

import java.io.File

class RunSingle extends Commons {
  val simpleTestsFolder = new File(testsPrefix + File.separator + "simple-tests" + File.separator)

  @Test def singleTest(): Unit = {
//    val testPath = "/home/bogus/py2eo/runEO/div.yaml"
//      val testPath = simpleTestsFolder + "/simple-statements/library/list-5.yaml"
    val testPath = simpleTestsFolder + "/simple-statements/break/nested_while_break1.yaml"
//    val testPath = simpleTestsFolder + "/expressions/lambda/lambda2.yaml"
//    val testPath = simpleTestsFolder + "/compound-statements/try/exceptions-finally-3.yaml"
//    val testPath = simpleTestsFolder + "/library/list/list-5.yaml"
    useCageHolder(new File(testPath))
  }
}