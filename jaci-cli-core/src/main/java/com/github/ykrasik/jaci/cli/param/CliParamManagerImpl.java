/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jaci.cli.param;

import com.github.ykrasik.jaci.cli.assist.ParamAssistInfo;
import com.github.ykrasik.jaci.cli.exception.ParseException;
import com.github.ykrasik.jaci.command.CommandArgs;
import com.github.ykrasik.jaci.util.trie.Trie;
import com.github.ykrasik.jaci.util.trie.TrieBuilder;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of a {@link CliParamManager}.
 * Delegates most calculations to a {@link CliParamParseContext}.
 *
 * @author Yevgeny Krasik
 */
@ToString(of = "params")
public class CliParamManagerImpl implements CliParamManager {
    private final List<CliParam> params;
    private final Trie<CliParam> paramsTrie;

    public CliParamManagerImpl(@NonNull List<CliParam> params) {
        this.params = Collections.unmodifiableList(params);
        this.paramsTrie = createParamsTrie(params);
    }

    private Trie<CliParam> createParamsTrie(List<CliParam> params) {
        final TrieBuilder<CliParam> builder = new TrieBuilder<>();
        for (CliParam param : params) {
            builder.add(param.getIdentifier().getName(), param);
        }
        return builder.build();
    }

    @Override
    public List<CliParam> getParams() {
        return params;
    }

    @Override
    public CommandArgs parse(List<String> args) throws ParseException {
        // Parse all args.
        final CliParamParseContext context = doParse(args);
        return context.createCommandArgs();
    }

    @Override
    // TODO: Consider changing this to the following: a params context object that is passed instead of a list.
    // TODO: It will live through the whole process and collect information from each call.
    // TODO: Essentially, an object much larger then CliParamParseContext, but possibly more convenient to use.
    public ParamAssistInfo assist(List<String> args) throws ParseException {
        // Only the last arg is up for auto-completion, the rest are expected to be valid args.
        // Parse all params that have been bound.
        final List<String> argsToBeParsed = args.subList(0, args.size() - 1);
        final CliParamParseContext context = doParse(argsToBeParsed);

        final String prefix = args.get(args.size() - 1);
        return context.createParamAssistInfo(prefix);
    }

    private CliParamParseContext doParse(List<String> args) throws ParseException {
        final CliParamParseContext context = new CliParamParseContext(params, paramsTrie);
        for (String arg : args) {
            context.parseValue(arg);
        }
        return context;
    }
}
