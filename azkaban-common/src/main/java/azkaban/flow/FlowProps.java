/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.flow;

import azkaban.utils.Props;
import java.util.HashMap;
import java.util.Map;

public class FlowProps {

  private String parentSource;
  private String propSource;
  private Props props = null;

  public FlowProps(final String parentSource, final String propSource) {
    /**
     * Use String interning so that just 1 copy of the string value exists in String Constant Pool
     * and the value is reused. Azkaban Heap dump analysis indicated a  high percentage of heap
     * usage is coming from duplicate strings of FlowProps fields.
     *
     * Using intern() eliminates all the duplicate values, thereby significantly reducing heap
     * memory usage.
     * s.intern()方法的时候，会将共享池中的字符串与外部的字符串(s)进行比较,如果共享池中有与之相等 的字符串，
     * 则不会将外部的字符串放到共享池中的，返回的只是共享池中的字符串，如果不同则将外部字符串放入共享池中，
     * 并返回其字符串的句柄（引用）-- 这样做的好处就是能够节约空间
     */
    if(parentSource != null) {
      this.parentSource = parentSource.intern();
    }
    if (propSource != null) {
      this.propSource = propSource.intern();
    }
  }

  public FlowProps(final Props props) {
    this.setProps(props);
  }

  public static FlowProps fromObject(final Object obj) {
    final Map<String, Object> flowMap = (Map<String, Object>) obj;
    final String source = (String) flowMap.get("source");
    final String parentSource = (String) flowMap.get("inherits");

    final FlowProps flowProps = new FlowProps(parentSource, source);
    return flowProps;
  }

  public Props getProps() {
    return this.props;
  }

  public void setProps(final Props props) {
    this.props = props;
    this.parentSource =
        props.getParent() == null ? null : props.getParent().getSource();
    this.propSource = props.getSource();
  }

  public String getSource() {
    return this.propSource;
  }

  public String getInheritedSource() {
    return this.parentSource;
  }

  public Object toObject() {
    final HashMap<String, Object> obj = new HashMap<>();
    obj.put("source", this.propSource);
    if (this.parentSource != null) {
      obj.put("inherits", this.parentSource);
    }
    return obj;
  }
}
