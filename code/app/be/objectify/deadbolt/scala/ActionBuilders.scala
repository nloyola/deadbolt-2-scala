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
package be.objectify.deadbolt.scala

import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.models.PatternType
import play.api.mvc._

import scala.concurrent.Future

/**
 * Provides helpers for creating Play Actions wrapped by DeadboltActions.
 */
@Singleton
class ActionBuilders @Inject() (deadboltActions: DeadboltActions, handlers: HandlerCache) {

  object RestrictAction {

    def apply(roles: RoleGroups): RestrictAction.RestrictActionBuilder = RestrictActionBuilder(roles)
    def apply(roles: String*): RestrictAction.RestrictActionBuilder = RestrictActionBuilder(List(roles.toArray))

    case class RestrictActionBuilder(roles: RoleGroups) extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.Restrict(roles, handler)(bodyParser)(block)
    }
  }

  object RoleBasedPermissionsAction {

    def apply(roleName: String): RoleBasedPermissionsAction.RoleBasedPermissionsActionBuilder = RoleBasedPermissionsActionBuilder(roleName)

    case class RoleBasedPermissionsActionBuilder(roleName: String) extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.RoleBasedPermissions(roleName, handler)(bodyParser)(block)
    }
  }

  object DynamicAction {

    def apply(name: String, meta: Option[Any] = None): DynamicAction.DynamicActionBuilder = DynamicActionBuilder(name, meta)

    case class DynamicActionBuilder(name: String, meta: Option[Any] = None) extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.Dynamic(name, meta, handler)(bodyParser)(block)
    }
  }

  object PatternAction {

    def apply(value: String, patternType: PatternType, meta: Option[Any] = None, invert: Boolean = false): PatternAction.PatternActionBuilder = PatternActionBuilder(value, patternType, meta, invert)

    case class PatternActionBuilder(value: String, patternType: PatternType = PatternType.EQUALITY, meta: Option[Any] = None, invert: Boolean = false) extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.Pattern(value, patternType, meta, handler, invert)(bodyParser)(block)
    }
  }

  object SubjectPresentAction {

    def apply(): SubjectPresentAction.SubjectPresentActionBuilder = SubjectPresentActionBuilder()

    case class SubjectPresentActionBuilder() extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.SubjectPresent(handler)(bodyParser)(block)
    }
  }

  object SubjectNotPresentAction {

    def apply(): SubjectNotPresentAction.SubjectNotPresentActionBuilder = SubjectNotPresentActionBuilder()

    case class SubjectNotPresentActionBuilder() extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.SubjectNotPresent(handler)(bodyParser)(block)
    }
  }

  object WithAuthRequestAction {

    def apply(): WithAuthRequestAction.WithAuthRequestActionBuilder = WithAuthRequestActionBuilder()

    case class WithAuthRequestActionBuilder() extends DeadboltActionBuilder {

      override def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler) : Action[A] =
        deadboltActions.WithAuthRequest(handler)(bodyParser)(block)
    }
  }

  trait DeadboltActionBuilder {

    def apply(block: => Future[Result])(implicit deadbloltHandler: DeadboltHandler): Action[AnyContent] = apply( _ => block)(deadbloltHandler)
    def apply(block: AuthenticatedRequest[AnyContent] => Future[Result])(implicit deadboltHandler: DeadboltHandler): Action[AnyContent] = apply(BodyParsers.parse.anyContent)(block)(deadboltHandler)
    def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])(implicit handler: DeadboltHandler): Action[A]

    def withHandler(deadboltHandler: DeadboltHandler) = new {
      def apply(block: => Future[Result]): Action[AnyContent] =
        DeadboltActionBuilder.this.apply(block)(deadboltHandler)

      def apply(block: AuthenticatedRequest[AnyContent] => Future[Result]): Action[AnyContent] =
        DeadboltActionBuilder.this.apply(block)(deadboltHandler)

      def apply[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result])  : Action[A] =
        DeadboltActionBuilder.this.apply(bodyParser)(block)(deadboltHandler)
    }

    def key(handlerKey: HandlerKey) = withHandler(handlers(handlerKey))

    def defaultHandler() = withHandler(handlers())
  }
}