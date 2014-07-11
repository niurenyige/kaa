/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.common.dao.impl.sql;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.APPLICATION_ALIAS;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.APPLICATION_PROPERTY;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.APPLICATION_REFERENCE;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.TOPIC_ALIAS;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.TOPICS_PROPERTY;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.TOPIC_REFERENCE;
import static org.kaaproject.kaa.server.common.dao.impl.sql.HibernateDaoConstants.WEIGHT_PROPERTY;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.kaaproject.kaa.server.common.dao.impl.EndpointGroupDao;
import org.kaaproject.kaa.server.common.dao.impl.TopicDao;
import org.kaaproject.kaa.server.common.dao.model.sql.EndpointGroup;
import org.kaaproject.kaa.server.common.dao.model.sql.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateEndpointGroupDao extends HibernateAbstractDao<EndpointGroup> implements EndpointGroupDao<EndpointGroup> {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateEndpointGroupDao.class);

    @Autowired
    private TopicDao<Topic> topicDao;

    @Override
    public List<EndpointGroup> findByApplicationId(String appId) {
        List<EndpointGroup> groups = null;
        LOG.debug("Find endpoint group by application id {} ", appId);
        if (isNotBlank(appId)) {
            groups = findListByCriterionWithAlias(APPLICATION_PROPERTY, APPLICATION_ALIAS,
                    Restrictions.eq(APPLICATION_REFERENCE, Long.valueOf(appId)));
        }
        return groups;
    }

    @Override
    public EndpointGroup findByAppIdAndWeight(String appId, int weight) {
        EndpointGroup group = null;
        LOG.debug("Find endpoint group by application id {} and weight {}", appId, weight);
        if (isNotBlank(appId)) {
            group = findOneByCriterionWithAlias(APPLICATION_PROPERTY, APPLICATION_ALIAS,
                    Restrictions.and(
                            Restrictions.eq(APPLICATION_REFERENCE, Long.valueOf(appId)),
                            Restrictions.eq(WEIGHT_PROPERTY, weight)));
        }
        return group;
    }

    @Override
    public EndpointGroup removeTopicFromEndpointGroup(String id, String topicId) {
        EndpointGroup endpointGroup = findById(id);
        Set<Topic> topics = endpointGroup.getTopics();
        Iterator<Topic> it = topics.iterator();
        while (it.hasNext()) {
            Topic topic = it.next();
            if (topic.getId() == Long.parseLong(topicId)) {
                it.remove();
                break;
            }
        }
        return save(endpointGroup);
    }

    @Override
    public List<EndpointGroup> findEndpointGroupsByTopicIdAndAppId(String appId, String topicId) {
        List<EndpointGroup> groups = null;
        LOG.debug("Find endpoint group by application id [{}] and topic id [{}]", appId, topicId);
        if (isNotBlank(appId)) {
            Criteria criteria = getCriteria();
            criteria.createAlias(APPLICATION_PROPERTY, APPLICATION_ALIAS);
            criteria.createAlias(TOPICS_PROPERTY, TOPIC_ALIAS);
            criteria.add(Restrictions.and(
                    Restrictions.eq(APPLICATION_REFERENCE, Long.valueOf(appId)),
                    Restrictions.eq(TOPIC_REFERENCE, Long.valueOf(topicId))));
            groups = findListByCriteria(criteria);
        }
        return groups;
    }

    @Override
    public EndpointGroup addTopicToEndpointGroup(String id, String topicId) {
        EndpointGroup endpointGroup = findById(id);
        Topic topic = topicDao.findById(topicId);
        Set<Topic> topics = endpointGroup.getTopics();
        topics.add(topic);
        return save(endpointGroup);
    }

    @Override
    protected Class<EndpointGroup> getEntityClass() {
        return EndpointGroup.class;
    }
}