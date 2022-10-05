package org.polystat.py2eo.transpiler

import org.junit.jupiter.api.Test

import java.io.File

class RunSingle extends Commons {
  val simpleTestsFolder = new File(testsPrefix + File.separator + "simple-tests" + File.separator)

  @Test def singleTest(): Unit = {
    val modulePath = simpleTestsFolder + "/simple-statements/import/own_module.yaml"
    useCageHolder(new File(modulePath), isModule = true)
    val testPath = simpleTestsFolder + "/simple-statements/import/import-3.yaml"
    useCageHolder(new File(testPath))
  }
}