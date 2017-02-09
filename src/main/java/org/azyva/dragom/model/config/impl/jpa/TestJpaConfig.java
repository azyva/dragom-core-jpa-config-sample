/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
 *
 * This file is part of Dragom.
 *
 * Dragom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Dragom.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.azyva.dragom.model.config.impl.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.azyva.dragom.model.config.MutableClassificationNodeConfig;
import org.azyva.dragom.model.config.MutableConfig;
import org.azyva.dragom.model.config.MutableModuleConfig;
import org.azyva.dragom.model.config.NodeConfig;
import org.azyva.dragom.model.config.NodeConfigTransferObject;
import org.azyva.dragom.model.config.impl.simple.SimplePluginDefConfig;
import org.azyva.dragom.model.config.impl.simple.SimplePropertyDefConfig;
import org.azyva.dragom.model.plugin.ArtifactInfoPlugin;
import org.azyva.dragom.model.plugin.ScmPlugin;

public class TestJpaConfig {
  public static void main(String[] args) {
    Path pathTestWorkspace;
    InputStream inputStream;
    EntityManagerFactory entityManagerFactory;
    Map<String, String> mapProperty;
    MutableConfig mutableConfig;
    MutableClassificationNodeConfig mutableClassificationNodeConfigRoot;
    NodeConfigTransferObject nodeConfigTransferObject;
    MutableModuleConfig mutableModuleConfig;
    MutableClassificationNodeConfig mutableClassificationNodeConfig;
    List<NodeConfig> listNodeConfig;

    if (args.length == 0) {
      pathTestWorkspace = Paths.get(System.getProperty("user.dir")).resolve("test-workspace");
      System.out.println("Test workspace directory not specified. Using \"test-workspace\" subdirectory of current directory " + pathTestWorkspace + '.');
    } else {
      pathTestWorkspace = Paths.get(args[0]);
      System.out.println("Using specified test workspace directory " + pathTestWorkspace + '.');

      args = Arrays.copyOfRange(args, 1, args.length);
    }

    try {
      inputStream = TestJpaConfig.class.getResourceAsStream("/dragom-ddl.sql");
      Files.copy(inputStream, pathTestWorkspace.resolve("dragom-ddl.sql"), StandardCopyOption.REPLACE_EXISTING);
      inputStream.close();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

    mapProperty = new HashMap<String, String>();

    mapProperty.put("javax.persistence.jdbc.url", "jdbc:h2:" + pathTestWorkspace.toAbsolutePath() + "/dragom;INIT=RUNSCRIPT FROM '" + pathTestWorkspace.toAbsolutePath().toString().replaceAll("\\\\", "/") + "/dragom-ddl.sql'");

    entityManagerFactory = Persistence.createEntityManagerFactory("dragom", mapProperty);

    try {

      /*
       * Initialize empty JpaConfig.
       */

      mutableConfig = new JpaConfig(entityManagerFactory);

      mutableClassificationNodeConfigRoot = (MutableClassificationNodeConfig)mutableConfig.getClassificationNodeConfigRoot();

      if (mutableClassificationNodeConfigRoot != null) {
        throw new RuntimeException("Root ClassificationNodeConfig must be null");
      }

      /*
       * Create root MutableClassificationNodeConfig and manipulate some
       * PropertyDefConfig and PluginDefConfig.
       */

      mutableClassificationNodeConfigRoot = mutableConfig.createMutableClassificationNodeConfigRoot();

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", null, false));
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY2", "VALUE2", true));
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.removePropertyDefConfig("PROPERTY");
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setPluginDefConfig(new SimplePluginDefConfig(ScmPlugin.class, null, "org.azyva.dragom.model.plugin.impl.GitScmPluginImpl", false));
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setPluginDefConfig(new SimplePluginDefConfig(ArtifactInfoPlugin.class, "PLUGIN_ID", "org.azyva.dragom.model.plugin.impl.SimpleArtifactInfoPluginImpl", true));
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.removePlugingDefConfig(ScmPlugin.class, null);
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Delete root MutableClassificationNodeConfig.
       */

      mutableClassificationNodeConfigRoot.delete();

      mutableClassificationNodeConfigRoot = (MutableClassificationNodeConfig)mutableConfig.getClassificationNodeConfigRoot();

      if (mutableClassificationNodeConfigRoot != null) {
        throw new RuntimeException("Root ClassificationNodeConfig must be null");
      }

      /*
       * Recreate root MutableClassificationNodeConfig.
       */

      mutableClassificationNodeConfigRoot = mutableConfig.createMutableClassificationNodeConfigRoot();

      nodeConfigTransferObject = mutableClassificationNodeConfigRoot.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setPropertyDefConfig(new SimplePropertyDefConfig("PROPERTY", "VALUE", false));
      nodeConfigTransferObject.setPluginDefConfig(new SimplePluginDefConfig(ScmPlugin.class, null, "org.azyva.dragom.model.plugin.impl.GitScmPluginImpl", false));
      mutableClassificationNodeConfigRoot.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Create a top-level MutableModuleConfig.
       */

      mutableModuleConfig = mutableClassificationNodeConfigRoot.createChildMutableModuleConfig();

      nodeConfigTransferObject = mutableModuleConfig.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setName("top-level-module");
      mutableModuleConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Create a domain MutableClassificationNodeConfig representing.
       */

      mutableClassificationNodeConfig = mutableClassificationNodeConfigRoot.createChildMutableClassificationNodeConfig();

      nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setName("Domain");
      mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Create a domain MutableModuleConfig.
       */

      mutableModuleConfig = mutableClassificationNodeConfig.createChildMutableModuleConfig();

      nodeConfigTransferObject = mutableModuleConfig.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setName("domain-module");
      mutableModuleConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Delete the domain MutableClassificationNodeConfig.
       */

      mutableClassificationNodeConfig.delete();

      /*
       * Recreate a domain MutableClassificationNodeConfig and domain
       * MutableModuleConfig.
       */

      mutableClassificationNodeConfig = mutableClassificationNodeConfigRoot.createChildMutableClassificationNodeConfig();

      mutableClassificationNodeConfigRoot = (MutableClassificationNodeConfig)mutableConfig.getClassificationNodeConfigRoot();

      nodeConfigTransferObject = mutableClassificationNodeConfig.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setName("Domain");
      mutableClassificationNodeConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      mutableModuleConfig = mutableClassificationNodeConfig.createChildMutableModuleConfig();

      nodeConfigTransferObject = mutableModuleConfig.getNodeConfigTransferObject(null);
      nodeConfigTransferObject.setName("domain-module");
      mutableModuleConfig.setNodeConfigTransferObject(nodeConfigTransferObject, null);

      /*
       * Initialize JpaConfig with non-empty DB.
       */

      mutableConfig = new JpaConfig(entityManagerFactory);

      mutableClassificationNodeConfigRoot = (MutableClassificationNodeConfig)mutableConfig.getClassificationNodeConfigRoot();

      listNodeConfig = mutableClassificationNodeConfigRoot.getListChildNodeConfig();
} catch (Exception e) {
      e.printStackTrace();
    } finally {
      entityManagerFactory.close();
    }

    System.exit(0);
  }
}
