/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core.code.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuarkusExtension {
    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("id")
    private String id;

    @JsonProperty("labels")
    private List<String> labels = new ArrayList<>();

    @JsonProperty("name")
    private String name;

    @JsonProperty("shortName")
    private String shortName;

    @JsonProperty("order")
    private int order;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();
    
    @JsonProperty("guide")
    private String guide;
    
    @JsonProperty("default")
    private boolean defaultExtension;
    
    @JsonProperty("shortId")
    private String shortId;
    
    @JsonProperty("providesExampleCode")
    private boolean providesExampleCode;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the guide
	 */
	public String getGuide() {
		return guide;
	}

	/**
	 * @param guide the guide to set
	 */
	public void setGuide(String guide) {
		this.guide = guide;
	}

	/**
	 * @return the default
	 */
	public boolean isDefaultExtension() {
		return defaultExtension;
	}

	/**
	 * @param defaultExtension the defaultExtension to set
	 */
	public void setDefaultExtension(boolean defaultExtension) {
		this.defaultExtension = defaultExtension;
	}

	/**
	 * @return the shortId
	 */
	public String getShortId() {
		return shortId;
	}

	/**
	 * @param shortId the shortId to set
	 */
	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	/**
   * @return the providesExampleCode
   */
  public boolean isProvidesExampleCode() {
    return providesExampleCode;
  }

  /**
   * @param providesExampleCode the providesExampleCode to set
   */
  public void setProvidesExampleCode(boolean providesExampleCode) {
    this.providesExampleCode = providesExampleCode;
  }

  public String asLabel() {
    StringBuilder builder = new StringBuilder(getName());
    List<String> tags = getTags();
    if (!tags.isEmpty()) {
      String labels = tags.stream().filter(tag -> !"provides-example".equals(tag))
          .collect(Collectors.joining(","));
      if (StringUtils.isNotBlank(labels)) {
        builder.append(" [").append(labels).append(']');
      }
    } else {
      String status = getStatus();
      if (StringUtils.isNotBlank(status) && !"stable".equalsIgnoreCase(status)) {
        builder.append(" [").append(status).append(']');
      }
    }
    return builder.toString();
	}
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuarkusExtension)) {
            return false;
        }
        QuarkusExtension other = (QuarkusExtension) obj;
        return Objects.equals(id, other.id);
    }
}
