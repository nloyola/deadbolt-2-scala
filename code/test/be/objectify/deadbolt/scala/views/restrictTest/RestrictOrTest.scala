/*
 * Copyright 2012-2015 Steve Chaloner
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
package be.objectify.deadbolt.scala.views.restrictTest

import be.objectify.deadbolt.scala.testhelpers.SecurityRole
import be.objectify.deadbolt.scala.views.AbstractViewTest
import be.objectify.deadbolt.scala.views.html.restrictTest.restrictOrContent
import play.api.test.{FakeRequest, Helpers, WithApplication}

/**
  * @author Steve Chaloner (steve@objectify.be)
  */
class RestrictOrTest extends AbstractViewTest {

   "when protected by a single role, the view" should {
     "hide constrained content and show fallback content when subject is not present" in new WithApplication(testApp(handler())) {
       val html = restrictOrContent(List(Array("foo")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have necessary role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("user"))))))) {
       val html = restrictOrContent(List(Array("admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have any roles" in new WithApplication(testApp(handler(subject = Some(user())))) {
       val html = restrictOrContent(List(Array("admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject has other roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("user"))))))) {
       val html = restrictOrContent(List(Array("admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has necessary role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"))))))) {
       val html = restrictOrContent(List(Array("admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }

   "when protected by two ANDed roles, the view" should {
     "hide constrained content and show fallback content when subject is not present" in new WithApplication(testApp(handler())) {
       val html = restrictOrContent(List(Array("admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have necessary role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("user"))))))) {
       val html = restrictOrContent(List(Array("admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have any roles" in new WithApplication(testApp(handler(subject = Some(user())))) {
       val html = restrictOrContent(List(Array("admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has both roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"), new SecurityRole("watchdog"))))))) {
       val html = restrictOrContent(List(Array("admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }

   "when protected by two ORed roles, the view" should {
     "hide constrained content and show fallback content when subject is not present" in new WithApplication(testApp(handler())) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have either of the necessary roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("user"))))))) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject does not have any roles" in new WithApplication(testApp(handler(subject = Some(user())))) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has both roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"), new SecurityRole("watchdog"))))))) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has one of the roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"))))))) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has another one of the roles" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("watchdog"))))))) {
       val html = restrictOrContent(List(Array("admin"), Array("watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }

   "when a single role is present and negated, the view" should {
     "hide constrained content and show fallback content when subject is not present" in new WithApplication(testApp(handler())) {
       val html = restrictOrContent(List(Array("!foo")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject has the negated role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"))))))) {
       val html = restrictOrContent(List(Array("!admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }/*
 * Copyright 2012-2015 Steve Chaloner
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


     "show constrained content and hide fallback content when subject does not have the negated role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("user"))))))) {
       val html = restrictOrContent(List(Array("!admin")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }

   "when ANDed roles contain a negated role, the view" should {
     "show constrained content and hide fallback content when subject is not present" in new WithApplication(testApp(handler())) {
       val html = restrictOrContent(List(Array("!admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "hide constrained content and show fallback content when subject has the negated role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("admin"), new SecurityRole("watchdog"))))))) {
       val html = restrictOrContent(List(Array("!admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must not contain("This is protected by the constraint.")
       content must contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }

     "show constrained content and hide fallback content when subject has the non-negated role but does not have the negated role" in new WithApplication(testApp(handler(subject = Some(user(roles = List(new SecurityRole("watchdog"))))))) {
       val html = restrictOrContent(List(Array("!admin", "watchdog")))(FakeRequest())

       private val content: String = Helpers.contentAsString(html)
       content must contain("This is before the constraint.")
       content must contain("This is protected by the constraint.")
       content must not contain("This is default content in case the constraint denies access to the protected content.")
       content must contain("This is after the constraint.")
     }
   }
 }
