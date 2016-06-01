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
package be.objectify.deadbolt.scala.cache

import javax.inject.Inject

import be.objectify.deadbolt.scala.composite.Constraint
import play.api.cache.CacheApi
import play.api.mvc.AnyContent

/**
  * @author Steve Chaloner (steve@objectify.be)
  */
class DefaultCompositeCache @Inject() (cache: CacheApi) extends CompositeCache {

  override def register(name: String, constraint: Constraint[AnyContent]): Unit = cache.set(s"Deadbolt.composite.$name", constraint)

  override def apply(name: String): Constraint[AnyContent] = {
    val maybeConstraint = cache.get[Constraint[AnyContent]](s"Deadbolt.composite.$name")
    maybeConstraint match {
      case Some(constraint) => constraint
      case None => throw new IllegalStateException(s"No composite constraint with name [$name] registered")
    }
  }
}
