// Copyright 2013 Thiago H. de Paula Figueiredo
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// Copyright 2013 Thiago H. de Paula Figueiredo
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.sixbuilder.twitterlib.helpers;

import org.apache.tapestry5.ComponentEventCallback;

/**
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class HolderComponentEventCallback<T> implements ComponentEventCallback<T> {
	
	private T result;

	public boolean handleResult(T result) {
		this.result = result;
		return result != null;
	}

	/**
	 * Returns the value of the result field.
	 * @return a {@link T}.
	 */
	final public T getResult() {
		return result;
	}

}
