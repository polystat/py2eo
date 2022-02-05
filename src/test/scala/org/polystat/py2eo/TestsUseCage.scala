package org.polystat.py2eo

import java.nio.file.{Files, Path, Paths}

import org.junit.Test

class TestsUseCage extends Commons {
  @Test def trivialTest():Unit = {
    val test = testsPrefix + "/trivial.yaml"
    useCageHolder(test,getYamlStr(test))
  }

  @Test def simplestClassTest():Unit = {
    val test = testsPrefix + "/simplestClass.yaml"
    useCageHolder(test,getYamlStr(test))
  }

  @Test def myListTest():Unit = {
    val test = testsPrefix + "/myList.yaml"
    useCageHolder(test,getYamlStr(test))
  }

  @Test def xTest():Unit = {
    val test = testsPrefix + "/x.yaml"
    useCageHolder(test,getYamlStr(test))
  }

  @Test def whileCheckTest():Unit = {
    Files.walk(Paths.get(testsPrefix + "/simple_tests/whileCheck")).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      useCageHolder(p.toString, getYamlStr(p.toString))
    })
  }

  @Test def ifCheck():Unit = {
    Files.walk(Paths.get(testsPrefix + "/simple_tests/ifCheck")).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      useCageHolder(p.toString, getYamlStr(p.toString))
    })
  }

  @Test def assignCheck():Unit = {
    Files.walk(Paths.get(testsPrefix + "/simple_tests/assignCheck")).filter((p: Path) => p.toString.endsWith(".yaml")).forEach((p: Path) => {
      useCageHolder(p.toString, getYamlStr(p.toString))
    })
  }
}
