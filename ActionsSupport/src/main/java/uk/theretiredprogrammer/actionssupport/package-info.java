/*
 * Copyright 2022 Richard Linsdale.
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
package uk.theretiredprogrammer.actionssupport;

/**
 * The ActionsSupport NBM is a set of classes which can be used by other
 * NetBeans plugins, to allow the creation of Actions, with the ability to run
 * external programs.
 *
 * This document is designed for developers of NetBeans Modules who wish to
 * integrate these features into their code.
 *
 * These Action can execute:
 *
 * NetBeans methods, Methods coded within the plugin
 *
 * External programs which can be executed in a CLI-like manner, allowing the
 * use of such programs from the NetBeans UI
 *
 * DynamicActions and DynamicAsyncActions
 *
 * A Dynamic Action is an Action which can be enabled or disabled and is not
 * displayed when disabled.
 *
 * These are two classes of Dynamic Actions:
 *
 * The base class (DynamicAction) which mirrors the Action class and the
 * DynamicAsyncAction class which mirrors the DynamicAction class, running the
 * onClick method in a separate thread, so being suitable for any action which
 * takes a period of time (which would otherwise block the processing of UI
 * events).
 *
 * DynamicActions can be associated with a particular node, either being for:
 * all projects of a particular type, a specific project, as defined by a
 * properties file using DynamicAyncActions.
 *
 * NodeActions supports the creation of Actions for a node, by enabling the
 * creation of DynamicCLIActions using a properties file.
 *
 * It observes the node folder containing the property file, ensuring the
 * actions are updated whenever changes occur to the properties file.
 *
 * Additional files within the node folder can be observed, so that changes can
 * trigger updates to any associated objects.
 *
 * NodeActions provides a method for assembling node actions, combining various
 * sources of actions to create the actions array required by a node definition.
 *
 */
