/*
   Copyright 2017 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ericsson.ei.jmespath.test;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestJmespathRestAPI {

    static Logger log = (Logger) LoggerFactory.getLogger(TestJmespathRestAPI.class);

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    private final String inputFilePath = "src/test/resources/EiffelArtifactCreatedEvent.json";

    private final String extractionRuleFilePath = "src/test/resources/ExtractionRule.txt";

    @Test
    public void testJmespathRestApi() throws Exception {

        String jsonInput = null;
        String extractionRules_test = null;
        try {
            jsonInput = FileUtils.readFileToString(new File(inputFilePath));
            extractionRules_test = FileUtils.readFileToString(new File(extractionRuleFilePath));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/jmespathrule/ruleCheck").accept(MediaType.ALL)
                .param("rule", extractionRules_test).param("jsonContent", jsonInput);
        // content(jsonInput).contentType(MediaType.ALL);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String resultStr = result.getResponse().getContentAsString().toString();

        JSONObject obj = new JSONObject(resultStr);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("e90daae3-bf3f-4b0a-b899-67834fd5ebd0", obj.getString("id"));
        assertEquals("1484061386383", obj.getString("time"));

    }

}
