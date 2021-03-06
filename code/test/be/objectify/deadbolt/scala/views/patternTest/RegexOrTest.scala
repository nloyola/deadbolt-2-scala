/*
 * Copyright 2012-2016 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.scala.views.patternTest

import be.objectify.deadbolt.scala.models.{PatternType, Subject}
import be.objectify.deadbolt.scala.{DynamicResourceHandler, AuthenticatedRequest}
import be.objectify.deadbolt.scala.testhelpers.SecurityPermission
import be.objectify.deadbolt.scala.views.{drh, AbstractViewTest}
import be.objectify.deadbolt.scala.views.html.patternTest.patternOrContent
import play.api.test.{FakeRequest, Helpers, WithApplication}

/**
  * @author Steve Chaloner (steve@objectify.be)
  */
class RegexOrTest extends AbstractViewTest {

  val userZombie: Option[Subject] = Some(user(permissions = List(SecurityPermission("killer.undead.zombie"))))
  val userFooBar: Option[Subject] = Some(user(permissions = List(SecurityPermission("killer.foo.bar"))))
  val drHandler: Option[DynamicResourceHandler] = Some(drh(allowed = true, check = true))

   "when the subject has a permission that matches the pattern, the view" should {
     "show constrained content and hide fallback content" in new WithApplication(testApp(handler(subject = userZombie, drh = drHandler))) {
       val html = patternOrContent(value = "killer.undead.*", patternType = PatternType.REGEX)(new AuthenticatedRequest(FakeRequest(), userZombie))

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }

  "when the subject has no permissions that match the pattern, the view" should {
    "hide constrained content and show fallback content" in new WithApplication(testApp(handler(subject = userFooBar, drh = drHandler))) {
      val html = patternOrContent(value = "killer.undead.*", patternType = PatternType.EQUALITY)(new AuthenticatedRequest(FakeRequest(), userFooBar))

      private val content: String = Helpers.contentAsString(html)
      content must contain("This is before the constraint.")
      content must not contain("This is protected by the constraint.")
      content must contain("This is default content in case the constraint denies access to the protected content.")
      content must contain("This is after the constraint.")
    }
  }
 }
