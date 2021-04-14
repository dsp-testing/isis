/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.wicket.ui.actionresponse;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestHandler;

import org.apache.isis.applib.value.OpenUrlStrategy;
import org.apache.isis.viewer.wicket.ui.pages.PageAbstract;

import lombok.NonNull;

/**
 * The response to provide as a result of interpreting the response;
 * either to show a {@link #toPage(PageAbstract) page}, or to 
 * {@link #withHandler(IRequestHandler) redirect} to a
 * handler (eg a download).
 */
public class ActionResultResponse {

    private final ActionResultResponseHandlingStrategy handlingStrategy;
    private final IRequestHandler handler;
    private final PageAbstract page;
    private final AjaxRequestTarget target;
    private final String url;

    public static ActionResultResponse withHandler(IRequestHandler handler) {
        return new ActionResultResponse(
                ActionResultResponseHandlingStrategy.SCHEDULE_HANDLER, handler, null, null, null);
    }
    
    public static ActionResultResponse toPage(PageAbstract page) {
        return new ActionResultResponse(
                ActionResultResponseHandlingStrategy.REDIRECT_TO_PAGE, null, page, null, null);
    }
    
    public static ActionResultResponse openUrlInBrowser(
            final AjaxRequestTarget target, 
            final String url, 
            final @NonNull OpenUrlStrategy openUrlStrategy) {
        return new ActionResultResponse(
                openUrlStrategy.isNewWindow()
                    ? ActionResultResponseHandlingStrategy.OPEN_URL_IN_NEW_BROWSER_WINDOW 
                    : ActionResultResponseHandlingStrategy.OPEN_URL_IN_SAME_BROWSER_WINDOW,
                null, null, target, url);
    }
    
    private ActionResultResponse(
            final ActionResultResponseHandlingStrategy strategy,
            final IRequestHandler handler,
            final PageAbstract page,
            final AjaxRequestTarget target,
            final String url) {
        this.handlingStrategy = strategy;
        this.handler = handler;
        this.page = page;
        this.target = target;
        this.url = url;
    }

    public ActionResultResponseHandlingStrategy getHandlingStrategy() {
        return handlingStrategy;
    }

    /**
     * Populated only if {@link #getHandlingStrategy() handling strategy} is {@link ActionResultResponseHandlingStrategy#SCHEDULE_HANDLER}
     */
    public IRequestHandler getHandler() {
        return handler;
    }
    
    /**
     * Populated only if {@link #getHandlingStrategy() handling strategy} is {@link ActionResultResponseHandlingStrategy#REDIRECT_TO_PAGE}
     */
    public PageAbstract getToPage() {
        return page;
    }
    
    /**
     * Populated only if {@link #getHandlingStrategy() handling strategy} is 
     * either {@link ActionResultResponseHandlingStrategy#OPEN_URL_IN_NEW_BROWSER_WINDOW}
     * or {@link ActionResultResponseHandlingStrategy#OPEN_URL_IN_SAME_BROWSER_WINDOW}
     */
    public AjaxRequestTarget getTarget() {
        return target;
    }
    
    /**
     * Populated only if {@link #getHandlingStrategy() handling strategy} is 
     * either {@link ActionResultResponseHandlingStrategy#OPEN_URL_IN_NEW_BROWSER_WINDOW}
     * or {@link ActionResultResponseHandlingStrategy#OPEN_URL_IN_SAME_BROWSER_WINDOW}
     */
    public String getUrl() {
        return url;
    }
}
