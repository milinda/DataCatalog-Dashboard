/**
 *  Copyright (c) 2011, Data to Insight Center. (http://pti.iu.edu/d2i) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.indiana.d2i.datacatalog;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Test {
    public static void main(String[] args) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());
        // Get XML
        System.out.println(service.path("hello").path("todo").accept(
                MediaType.TEXT_XML).get(String.class));
        // Get XML for application
        System.out.println(service.path("hello").path("todo").accept(
                MediaType.APPLICATION_JSON).get(String.class));
        // Get JSON for application
        System.out.println(service.path("hello").path("todo").accept(
                MediaType.APPLICATION_XML).get(String.class));
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri(
                "http://localhost:8080/datacatalog-dashboard").build();
    }
}
