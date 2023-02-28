/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.util;

/**
 * An Exception used within the ActionsAndActivities Library.
 *
 */
public class ApplicationException extends Exception {

    /**
     * Create a basic ApplicationException
     */
    public ApplicationException() {
        super();
    }

    /**
     * Create an ApplicationException with a String payroll
     *
     * @param message the payroll
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Create an ApplicationException with a String payroll and a previously
     * caught Throwable.
     *
     * @param message the payrol
     * @param cause the Throwable to attach
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
