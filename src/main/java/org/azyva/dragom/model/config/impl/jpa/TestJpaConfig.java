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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestJpaConfig {
  public static void main(String[] args) {
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    entityManagerFactory = null;
    entityManager = null;

    try {
      entityManagerFactory = Persistence.createEntityManagerFactory("dragom");
      entityManager = entityManagerFactory.createEntityManager();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }

      if (entityManagerFactory != null) {
        entityManagerFactory.close();
      }
    }

    System.exit(0);
  }
}
