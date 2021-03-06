//  Gant -- A Groovy way of scripting Ant tasks.
//
//  Copyright © 2006–2010, 2013  Russel Winder
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package org.codehaus.gant.tests

/**
 *  A test to ensure that the target executing works.
 *
 *  @author Russel Winder <russel@winder.org.uk>
 */
final class ExecutingTargets_Test extends GantTestCase {
  final targetName = 'testing'
  final clean = 'clean'
  final something = 'something'
  final somethingElse = 'somethingElse'
  final coreScript = """
target(${something}: '') { }
target(${somethingElse}: '') { }
"""
  void testSomethingArgs() {
    script = coreScript
    assertEquals(0, gant.processArgs(['-f',  '-', something] as String[]))
    assertEquals(resultString(something, ''), output)
    assertEquals('', error)
  }
  void testSomethingTargets() {
    script = coreScript
    assertEquals(0, processCmdLineTargets(something))
    assertEquals(resultString(something, ''), output)
    assertEquals('', error)
  }
  void testCleanAndSomethingArgs() {
    script = 'includeTargets << gant.targets.Clean\n' + coreScript
    assertEquals(0, gant.processArgs(['-f',  '-', clean, something] as String[]))
    assertEquals(resultString(clean, '') + resultString(something, ''), output)
    assertEquals('', error)
  }
  void testCleanAndSomethingTargets() {
    script = 'includeTargets << gant.targets.Clean\n' + coreScript
    assertEquals(0, processCmdLineTargets([ clean, something ]))
    assertEquals(resultString(clean, '') + resultString(something, ''), output)
    assertEquals('', error)
  }

 //  GANT-44 asks for targets to have access to the command line target list so that it can be processed in targets.

  void testTargetsListIsAccessbileAnChangeable() {
    script = """
target(${targetName}: '') {
  assert targets.class == ArrayList
  assert targets.size() == 3
  assert targets[0] == 'testing'
  assert targets[1] == 'one'
  assert targets[2] == 'two'
  def x = targets.remove(1)
  assert x == 'one'
  assert targets.size() == 2
  assert targets[0] == 'testing'
  assert targets[1] == 'two'
}
"""
    assertEquals(-11, processCmdLineTargets([ targetName, 'one', 'two' ]))
    assertEquals(resultString(targetName, ''), output)
    assertEquals('Target two does not exist.\n', error)
  }

  //  GANT-81 requires that the target finalize is called in all circumstances if it is present.  If it
  //  contains dependencies then they are ignored.

  private final testingMessage =  'testing called'
  private final finalizeMessage = 'finalize called'
  private final finalize = 'finalize'
  private final burble = 'burble'
  void testFinalizeIsCalledNormally() {
    script = """
target(${targetName}: '') { println('${testingMessage}') }
target(${finalize}: '') { println('${finalizeMessage}') }
"""
    assertEquals(0, processCmdLineTargets(targetName))
    assertEquals(resultString(targetName, testingMessage + '\n') + resultString(finalize, finalizeMessage + '\n'), output)
    assertEquals('', error)
  }
  void testFinalizeIsCalledOnAnException() {
    script = """
target(${targetName}: '') { throw new RuntimeException('${testingMessage}') }
target(${finalize}: '') { println('${finalizeMessage}') }
"""
    assertEquals(-13, processCmdLineTargets(targetName))
    assertEquals(targetName + ':\n' + resultString(finalize, finalizeMessage + '\n'), output)
    assertEquals("java.lang.RuntimeException: ${testingMessage}\n", error)
  }
  void testUsingSetFinalizerFinalizeIsCalledNormally() {
    script = """
target(${targetName}: '') { println('${testingMessage}') }
target(burble: '') { println('${finalizeMessage}') }
setFinalizeTarget(burble)
"""
    assertEquals(0, processCmdLineTargets(targetName))
    assertEquals(resultString(targetName, testingMessage + '\n') + resultString(burble, finalizeMessage + '\n'), output)
    assertEquals('', error)
  }
  void testUsingSetFinalizerFinalizeIsCalledOnAnException() {
    script = """
target(${targetName}: '') { throw new RuntimeException('${testingMessage}') }
target(burble: '') { println('${finalizeMessage}') }
setFinalizeTarget(burble)
"""
    assertEquals(-13, processCmdLineTargets(targetName))
    assertEquals(targetName + ':\n' + resultString(burble, finalizeMessage + '\n'), output)
    assertEquals("java.lang.RuntimeException: ${testingMessage}\n", error)
  }
  void testReturnValueFromOneTargetReceivedByCaller() {
    final called = 'called'
    script = """
target(${called}: '') { 17 }
target(${targetName}: '') { assert ${called}() == 17 }
"""
    assertEquals(0, processCmdLineTargets(targetName))
    assertEquals(resultString(targetName, resultString(called, '')), output)
    assertEquals('', error)
  }
}
