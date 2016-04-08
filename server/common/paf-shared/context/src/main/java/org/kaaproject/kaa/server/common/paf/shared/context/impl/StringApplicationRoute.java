/**
 * Copyright 2014-2016 CyberVision, Inc.
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

package org.kaaproject.kaa.server.common.paf.shared.context.impl;

import org.kaaproject.kaa.server.common.paf.shared.context.ApplicationRoute;

public class StringApplicationRoute implements ApplicationRoute {

    private final String strRoute;

    public StringApplicationRoute(String strRoute) {
        super();
        this.strRoute = strRoute;
    }

    public String getStrRoute() {
        return strRoute;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((strRoute == null) ? 0 : strRoute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StringApplicationRoute other = (StringApplicationRoute) obj;
        if (strRoute == null) {
            if (other.strRoute != null)
                return false;
        } else if (!strRoute.equals(other.strRoute))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StringApplicationRoute [strRoute=" + strRoute + "]";
    }
    
}