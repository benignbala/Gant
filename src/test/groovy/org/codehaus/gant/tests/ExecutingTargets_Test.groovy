//  Gant -- A Groovy build framework based on scripting Ant tasks.
//
//  Copyright © 2006-7 Russel Winder
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
 *  @author Russel Winder <russel.winder@concertant.com>
 */
final class ExecutingTargets_Test extends GantTestCase {
  final coreScript = '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
'''
  void testSomething ( ) {
    System.setIn ( new StringBufferInputStream ( coreScript ) )
    assertEquals ( 0 , gant.process ( [ '-f' ,  '-' , 'something' ] as String[] ) )
    assertEquals ( '' , output ) 
  }
  void testCleanAndSomething ( ) {
    System.setIn ( new StringBufferInputStream ( 'includeTargets << gant.targets.Clean\n' + coreScript ) )
    assertEquals ( 0 , gant.process ( [ '-f' ,  '-' , 'clean' , 'something' ] as String[] ) )
    assertEquals ( '' , output ) 
  }
}
