/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.request.builder.sse

import com.ning.http.client.Request
import io.gatling.core.session._
import io.gatling.http.{ HeaderValues, HeaderNames }
import io.gatling.http.action.sse.SseGetActionBuilder
import io.gatling.http.config.HttpProtocol
import io.gatling.http.request.builder.{ RequestBuilder, CommonAttributes }

object SseGetRequestBuilder {

  val SseHeaderValueExpression = HeaderValues.TextEventStream.expression
  val CacheControlNoCacheValueExpression = HeaderValues.NoCache.expression

  def apply(requestName: Expression[String], url: Expression[String], sseName: String) =
    new SseGetRequestBuilder(CommonAttributes(requestName, "GET", Left(url)), sseName)
      .header(HeaderNames.Accept, SseHeaderValueExpression)
      .header(HeaderNames.CacheControl, CacheControlNoCacheValueExpression)

  implicit def toActionBuilder(requestBuilder: SseGetRequestBuilder) = new SseGetActionBuilder(requestBuilder.commonAttributes.requestName, requestBuilder.sseName, requestBuilder)
}

class SseGetRequestBuilder(commonAttributes: CommonAttributes, val sseName: String) extends RequestBuilder[SseGetRequestBuilder](commonAttributes) {

  override private[http] def newInstance(commonAttributes: CommonAttributes): SseGetRequestBuilder = new SseGetRequestBuilder(commonAttributes, sseName)

  def build(protocol: HttpProtocol): Expression[Request] = new SseRequestExpressionBuilder(commonAttributes, protocol).build
}