/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.ioc.speaker;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import org.b3log.latke.ioc.speaker.annotation.Hello;
import org.b3log.latke.ioc.speaker.annotation.Midnight;
import org.b3log.latke.ioc.speaker.annotation.Morning;
import org.b3log.latke.ioc.speaker.annotation.Night;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 17, 2009
 */
public class SpeakerService {

    // MidnightSpeaker Providers
    /**
     * Injected by {@literal <type: Speaker>}.
     */
    @Inject
    Provider<MidnightSpeaker> midnightSpeakerProvider1;
    /**
     * Injected by {@literal <type: NightSpeaker, qualifiers: {Midnight}>}.
     */
    @Inject
    @Midnight
    Provider<NightSpeaker> midnightSpeakerProvider2;
    /**
     * Injected by {@literal <type: Speaker, qualifiers: {Midnight}>}.
     */
    @Inject
    @Midnight
    Provider<Speaker> midnightSpeakerProvider3;
    /**
     * Injected by {@literal <type: Speaker, qualifiers: {Named("midnightSpeaker")}>}.
     */
    @Inject
    @Named("midnightSpeaker")
    Provider<Speaker> midnightSpeakerProvider4;
    /**
     * Injected by {@literal <type: NightSpeaker, qualifiers: {Named("midnightSpeaker")}>}.
     */
    @Inject
    @Named("midnightSpeaker")
    Provider<NightSpeaker> midnightSpeakerProvider5;
    /**
     * Injected by {@literal <type: DarknessSpeaker, qualifiers: {Named("midnightSpeaker")}>}.
     */
    @Inject
    @Named("midnightSpeaker")
    Provider<DarknessSpeaker> midnightSpeakerProvider6;
    // NightSpeaker Providers
    /**
     * Injected by {@literal <type: NightSpeaker>}.
     */
    @Inject
    Provider<NightSpeaker> nightSpeakerProvider1;
    /**
     * Injected by {@literal <type: NightSpeaker, qualifiers: {Night}>}.
     */
    @Inject
    @Night
    Provider<NightSpeaker> nightSpeakerProvider2;
    /**
     * Injected by {@literal <type: DarknessSpeaker, qualifiers: {Night}>}.
     */
    @Inject
    @Night
    Provider<DarknessSpeaker> nightSpeakerProvider3;
    /**
     * Injected by {@literal <type: Speaker, qualifiers: {Night}>}.
     */
    @Inject
    @Night
    Provider<Speaker> nightSpeakerProvider4;
    // MorningSpeaker Providers
    /**
     * Injected by {@literal <type: MorningSpeaker}.
     */
    @Inject
    Provider<MorningSpeaker> morningSpeakerProvider1;
    /**
     * Injected by {@literal <type: Speaker, qualifiers: {Morning}>}.
     */
    @Inject
    @Morning
    Provider<Speaker> morningSpeakerProvider2;
    // HelloSpeaker Providers
    /**
     * Injected by {@literal <type: HelloSepaker>}.
     */
    @Inject
    Provider<HelloSpeaker> helloSpeakerProvider1;
    /**
     * Injected by {@literal <type: Speaker, qualifiers: {Hello}>}.
     */
    @Inject
    @Hello
    Provider<Speaker> helloSpeakerProvider2;
    /**
     * Injected by
     * {@literal <type: Speaker, qualifiers: {Named("helloSpeaker")}>}.
     */
    @Inject
    @Named("helloSpeaker")
    Provider<Speaker> helloSpeakerProvider3;
    /**
     * Injected by
     * {@literal <type: Speaker, qualifiers: {Named("helloSpeaker"), Hello}>}.
     */
    @Inject
    @Named("helloSpeaker")
    @Hello
    Provider<Speaker> helloSpeakerProvider4;
    /**
     * Injected by
     * {@literal <type: HelloSpeaker, qualifiers: {Named("helloSpeaker")}>}.
     */
    @Inject
    @Named("helloSpeaker")
    Provider<HelloSpeaker> helloSpeakerProvider5;
    /**
     * Injected by
     * {@literal <type: HelloSpeaker, qualifiers: {Named("helloSpeaker"), Hello}>}.
     */
    @Inject
    @Named("helloSpeaker")
    @Hello
    Provider<HelloSpeaker> helloSpeakerProvider6;
    // TODO: Illegal field injection points should be check at configuring
//    @Inject
    Provider<Speaker> illegalHelloSpeakerProvider1;
}
