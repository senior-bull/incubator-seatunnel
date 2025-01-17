/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connector.common.schema;

import org.apache.seatunnel.api.table.type.ArrayType;
import org.apache.seatunnel.api.table.type.BasicType;
import org.apache.seatunnel.api.table.type.DecimalType;
import org.apache.seatunnel.api.table.type.MapType;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.connectors.seatunnel.common.schema.SeatunnelSchema;

import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigFactory;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigResolveOptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class SchemaParseTest {

    @Test
    public void testSimpleSchemaParse() throws FileNotFoundException, URISyntaxException {
        String path = getTestConfigFile("/simple.schema.conf");
        Config config = ConfigFactory
                .parseFile(new File(path))
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(), ConfigResolveOptions.defaults().setAllowUnresolved(true));
        config = config.getConfig("schema");
        SeatunnelSchema seatunnelSchema = SeatunnelSchema.buildWithConfig(config);
        SeaTunnelRowType seaTunnelRowType = seatunnelSchema.getSeaTunnelRowType();
        Assertions.assertNotNull(seatunnelSchema);
        Assertions.assertEquals(seaTunnelRowType.getFieldType(1), ArrayType.BYTE_ARRAY_TYPE);
        Assertions.assertEquals(seaTunnelRowType.getFieldType(2), BasicType.STRING_TYPE);
        Assertions.assertEquals(seaTunnelRowType.getFieldType(10), new DecimalType(30, 8));
    }

    @Test
    public void testComplexSchemaParse() throws FileNotFoundException, URISyntaxException {
        String path = getTestConfigFile("/complex.schema.conf");
        Config config = ConfigFactory
                .parseFile(new File(path))
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(), ConfigResolveOptions.defaults().setAllowUnresolved(true));
        config = config.getConfig("schema");
        SeatunnelSchema seatunnelSchema = SeatunnelSchema.buildWithConfig(config);
        SeaTunnelRowType seaTunnelRowType = seatunnelSchema.getSeaTunnelRowType();
        Assertions.assertNotNull(seatunnelSchema);
        Assertions.assertEquals(seaTunnelRowType.getFieldType(0),
                new MapType<>(BasicType.STRING_TYPE, new MapType<>(BasicType.STRING_TYPE, BasicType.STRING_TYPE)));
        Assertions.assertEquals(seaTunnelRowType.getFieldType(1),
                new MapType<>(BasicType.STRING_TYPE, new MapType<>(BasicType.STRING_TYPE, ArrayType.INT_ARRAY_TYPE)));
    }

    public static String getTestConfigFile(String configFile) throws FileNotFoundException, URISyntaxException {
        URL resource = SchemaParseTest.class.getResource(configFile);
        if (resource == null) {
            throw new FileNotFoundException("Can't find config file: " + configFile);
        }
        return Paths.get(resource.toURI()).toString();
    }

}
