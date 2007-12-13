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
 *  A test to ensure that the target listing works. 
 *
 *  @author Russel Winder <russel.winder@concertant.com>
 */
final class Targets_Test extends GantTestCase {
  final coreScript = '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
'''
  void testSomething ( ) {
    System.setIn ( new StringBufferInputStream ( coreScript ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 something      Do something.
 somethingElse  Do something else.

''' , output ) 
  }
  void testSomethingAndClean ( ) {
    System.setIn ( new StringBufferInputStream ( 'includeTargets << gant.targets.Clean\n' + coreScript ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 clean          Action the cleaning.
 clobber        Action the clobbering.  Do the cleaning first.
 something      Do something.
 somethingElse  Do something else.

''' , output ) 
  }
  void testGStrings ( ) {
    System.setIn ( new StringBufferInputStream ( '''
def theWord = 'The Word'
target ( something : "Do ${theWord}." ) { }
target ( somethingElse : "Do ${theWord}." ) { }
''' ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 something      Do The Word.
 somethingElse  Do The Word.

''' , output ) 
  }
  void testDefaultSomething ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
target ( 'default' : 'something' ) { something ( ) }
''' ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 something      Do something.
 somethingElse  Do something else.

Default target is something.

''' , output ) 
  }  
  void testDefaultSomethingSetDefaultClosure ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
setdefault ( something )
''' ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 something      Do something.
 somethingElse  Do something else.

Default target is something.

''' , output ) 
  }  
  void testDefaultSomethingSetDefaultString ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
setdefault ( 'something' )
''' ) )
    assertEquals ( 0 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''
 something      Do something.
 somethingElse  Do something else.

Default target is something.

''' , output ) 
  }  
  void testDefaultSomethingSetDefaultFail ( ) {
    System.setIn ( new StringBufferInputStream ( '''
target ( something : "Do something." ) { }
target ( somethingElse : "Do something else." ) { }
setdefault ( 'fail' )
''' ) )
    assertEquals ( 2 , gant.process ( [ '-T' ,  '-f' ,  '-' ] as String[] ) )
    assertEquals ( '''Standard input, line 4 -- Error evaluating Gantfile: Target fail does not exist so cannot be made the default.
''' , output ) 
  }  
}