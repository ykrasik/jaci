///******************************************************************************
// * Copyright (C) 2014 Yevgeny Krasik                                          *
// *                                                                            *
// * Licensed under the Apache License, Version 2.0 (the "License");            *
// * you may not use this file except in compliance with the License.           *
// * You may obtain a copy of the License at                                    *
// *                                                                            *
// * http://www.apache.org/licenses/LICENSE-2.0                                 *
// *                                                                            *
// * Unless required by applicable law or agreed to in writing, software        *
// * distributed under the License is distributed on an "AS IS" BASIS,          *
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
// * See the License for the specific language governing permissions and        *
// * limitations under the License.                                             *
// ******************************************************************************/
//
//package com.github.ykrasik.jemi.core.annotation.command;
//
//import com.github.ykrasik.jemi.api.*;
//import com.github.ykrasik.jemi.core.annotation.param.AnnotationParamFactory;
//import com.github.ykrasik.jemi.core.command.CommandArgs;
//import com.github.ykrasik.jemi.core.command.CommandDef;
//import com.github.ykrasik.jemi.util.opt.Opt;
//import com.github.ykrasik.jemi.util.reflection.ReflectionParameter;
//import com.github.ykrasik.jemi.util.reflection.ReflectionUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.LinkedList;
//
//import static org.junit.Assert.*;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyObject;
//import static org.mockito.Mockito.*;
//
///**
// * @author Yevgeny Krasik
// */
//@RunWith(MockitoJUnitRunner.class)
//public class AnnotationCommandFactoryTest {
//    private AnnotationCommandFactory factory;
//    private AnnotationCommandTestClass instance;
//
//    private Method method;
//    private CommandDef command;
//
//    @Mock private AnnotationParamFactory paramFactory;
//
//    @Before
//    public void setUp() {
//        factory = new AnnotationCommandFactory(paramFactory);
//        instance = new AnnotationCommandTestClass();
//    }
//
//    @Test
//    public void testNoOutputPrinterNoArgs() {
//        assertCommand("noOutputPrinterNoArgs");
//        assertNoParams();
//
//        execute();
//        assertTrue(instance.noOutputPrinterNoArgsExecuted);
//    }
//
//    @Test
//    public void testNoArgs() {
//        assertCommand("noArgs");
//        assertNoParams();
//
//        execute();
//        assertTrue(instance.noArgsExecuted);
//    }
//
//    @Test
//    public void testCustomName() {
//        command = createCommand("commandCustomName").get();
//        assertEquals("customName", command.getName());
//        assertEquals("Command", command.getDescription());
//        assertNoParams();
//
//        execute();
//        assertTrue(instance.customNameExecuted);
//    }
//
//    @Test
//    public void testCustomDescription() {
//        assertCommand("customDescription", "custom description");
//        assertNoParams();
//
//        execute();
//        assertTrue(instance.customDescriptionExecuted);
//    }
//
//    @Test
//    public void testBoolParam() {
//        assertCommand("boolParam");
//        assertParams(CommandOutput.class, Boolean.TYPE);
//
//        execute(true);
//        assertTrue(instance.boolParamExecuted);
//    }
//
//    @Test
//    public void testBoolParamNoOutputPrinter() {
//        assertCommand("boolParamNoOutputPrinter");
//        assertParams(Boolean.class);
//
//        execute(false);
//        assertTrue(instance.boolParamNoOutputPrinterExecuted);
//    }
//
//    @Test
//    public void testDoubleParamIntParam() {
//        assertCommand("doubleParamIntParam");
//        assertParams(CommandOutput.class, Double.TYPE, Integer.TYPE);
//
//        execute(2.0, -5);
//        assertTrue(instance.doubleParamIntParamExecuted);
//    }
//
//    @Test
//    public void testDoubleParamIntParamNoOutputPrinter() {
//        assertCommand("doubleParamIntParamNoOutputPrinter");
//        assertParams(Double.class, Integer.class);
//
//        execute(-2.0, 999);
//        assertTrue(instance.doubleParamIntParamNoOutputPrinterExecuted);
//    }
//
//    @Test
//    public void testFlagParamStaticStringParamDynamicStringParam() {
//        assertCommand("flagParamStaticStringParamDynamicStringParam");
//        assertParams(CommandOutput.class, Boolean.TYPE, String.class, String.class);
//
//        execute(true, "string1", "string2");
//        assertTrue(instance.flagParamStaticStringParamDynamicStringParamExecuted);
//    }
//
//    @Test
//    public void testFlagParamStaticStringParamDynamicStringParamNoOutputPrinter() {
//        assertCommand("flagParamStaticStringParamDynamicStringParamNoOutputPrinter");
//        assertParams(Boolean.class, String.class, String.class);
//
//        execute(false, "string3", "string4");
//        assertTrue(instance.flagParamStaticStringParamDynamicStringParamNoOutputPrinterExecuted);
//    }
//
//    @Test
//    public void testAllParamTypes() {
//        assertCommand("allParamTypes");
//        assertParams(CommandOutput.class, Boolean.TYPE, Double.TYPE, Integer.TYPE, Boolean.TYPE, String.class, String.class);
//
//        execute(true, 1.3, 999, false, "string1", "string2");
//        assertTrue(instance.allParamTypesExecuted);
//    }
//
//    @Test
//    public void testAllParamTypesNoOutputPrinter() {
//        assertCommand("allParamTypesNoOutputPrinter");
//        assertParams(Boolean.class, Double.class, Integer.class, Boolean.class, String.class, String.class);
//
//        execute(false, -1.3, -999, true, "string3", "string4");
//        assertTrue(instance.allParamTypesNoOutputPrinterExecuted);
//    }
//
//    @Test
//    public void testToggleCommand() {
//        assertCommand("toggleCommand", "Toggle command");
//        assertNoParams();
//
//        execute(true);
//        assertTrue(instance.toggleCommandExecuted);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testInvalidToggleCommand1() {
//        createCommand("invalidToggleCommand1");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testInvalidToggleCommand2() {
//        createCommand("invalidToggleCommand2");
//    }
//
//    @Test
//    public void testNoAnnotation() {
//        assertFalse(createCommand("noAnnotation").isPresent());
//    }
//
//    private void assertCommand(String name) {
//        // Use default description
//        assertCommand(name, "Command");
//    }
//
//    private void assertCommand(String name, String description) {
//        command = createCommand(name).get();
//        assertEquals(name, command.getName());
//        assertEquals(description, command.getDescription());
//    }
//
//    private Opt<CommandDef> createCommand(String name) {
//        method = ReflectionUtils.lookupMethod(instance.getClass(), name);
//        return factory.createCommand(instance, method);
//    }
//
//    private void assertNoParams() {
//        verify(paramFactory, never()).createParam(anyObject(), any(ReflectionParameter.class));
//    }
//
//    private void assertParams(Class<?>... paramTypes) {
//        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
//        for (int i = 0; i < paramTypes.length; i++) {
//            final Annotation[] annotations = parameterAnnotations[i];
//            final Class<?> paramType = paramTypes[i];
//            if (paramType != CommandOutput.class) {
//                final ReflectionParameter param = new ReflectionParameter(paramType, annotations, i);
//                verify(paramFactory).createParam(instance, param);
//            }
//        }
//    }
//
//    private void execute(Object... args) {
//        final CommandArgs commandArgs = mock(CommandArgs.class);
//        when(commandArgs.getArgs()).thenReturn(new LinkedList<>(Arrays.asList(args)));
//        final CommandOutput outputPrinter = mock(CommandOutput.class);
//
//        try {
//            command.execute(commandArgs, outputPrinter);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static class AnnotationCommandTestClass {
//        public boolean noOutputPrinterNoArgsExecuted;
//        public boolean noArgsExecuted;
//        public boolean customNameExecuted;
//        public boolean customDescriptionExecuted;
//        public boolean boolParamExecuted;
//        public boolean boolParamNoOutputPrinterExecuted;
//        public boolean doubleParamIntParamExecuted;
//        public boolean doubleParamIntParamNoOutputPrinterExecuted;
//        public boolean flagParamStaticStringParamDynamicStringParamExecuted;
//        public boolean flagParamStaticStringParamDynamicStringParamNoOutputPrinterExecuted;
//        public boolean allParamTypesExecuted;
//        public boolean allParamTypesNoOutputPrinterExecuted;
//        public boolean toggleCommandExecuted;
//
//        @Command
//        public void noOutputPrinterNoArgs() {
//            noOutputPrinterNoArgsExecuted = true;
//        }
//
//        @Command
//        public void noArgs(CommandOutput commandOutput) {
//            noArgsExecuted = true;
//        }
//
//        @Command("customName")
//        public void commandCustomName() {
//            customNameExecuted = true;
//        }
//
//        @Command(description = "custom description")
//        public void customDescription() {
//            customDescriptionExecuted = true;
//        }
//
//        @Command
//        public void boolParam(CommandOutput commandOutput, @BoolParam boolean bool) {
//            boolParamExecuted = true;
//        }
//
//        @Command
//        public void boolParamNoOutputPrinter(@BoolParam Boolean bool) {
//            boolParamNoOutputPrinterExecuted = true;
//        }
//
//        @Command
//        public void doubleParamIntParam(CommandOutput commandOutput, @DoubleParam double doubleParam, @IntParam int intParam) {
//            doubleParamIntParamExecuted = true;
//        }
//
//        @Command
//        public void doubleParamIntParamNoOutputPrinter(@DoubleParam Double doubleParam, @IntParam Integer intParam) {
//            doubleParamIntParamNoOutputPrinterExecuted = true;
//        }
//
//        @Command
//        public void flagParamStaticStringParamDynamicStringParam(CommandOutput commandOutput,
//                                                                 @FlagParam boolean flag,
//                                                                 @StringParam String staticString,
//                                                                 @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
//            flagParamStaticStringParamDynamicStringParamExecuted = true;
//        }
//
//        @Command
//        public void flagParamStaticStringParamDynamicStringParamNoOutputPrinter(@FlagParam Boolean flag,
//                                                                                @StringParam String staticString,
//                                                                                @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
//            flagParamStaticStringParamDynamicStringParamNoOutputPrinterExecuted = true;
//        }
//
//        @Command
//        public void allParamTypes(CommandOutput commandOutput,
//                                  @BoolParam boolean bool,
//                                  @DoubleParam double doubleParam,
//                                  @IntParam int intParam,
//                                  @FlagParam boolean flag,
//                                  @StringParam String staticString,
//                                  @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
//            allParamTypesExecuted = true;
//        }
//
//        @Command
//        public void allParamTypesNoOutputPrinter(@BoolParam Boolean bool,
//                                                 @DoubleParam Double doubleParam,
//                                                 @IntParam Integer intParam,
//                                                 @FlagParam Boolean flag,
//                                                 @StringParam String staticString,
//                                                 @DynamicStringParam(supplier = "dummySupplier") String dynamicString) {
//            allParamTypesNoOutputPrinterExecuted = true;
//        }
//
//        @ToggleCommand
//        public ToggleCommandStateAccessor toggleCommand() {
//            return new ToggleCommandStateAccessor() {
//                @Override
//                public void set(boolean value) {
//                    toggleCommandExecuted = true;
//                }
//
//                @Override
//                public boolean get() {
//                    return false;
//                }
//            };
//        }
//
//        @ToggleCommand
//        public void invalidToggleCommand() {
//
//        }
//
//        @ToggleCommand
//        public ToggleCommandStateAccessor invalidToggleCommand2(int invalid) {
//            return mock(ToggleCommandStateAccessor.class);
//        }
//
//        public void noAnnotation() {
//
//        }
//    }
//}
