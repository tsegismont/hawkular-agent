/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.wildfly.agent.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jboss.logging.Logger;

/**
 * Installer values to be used for installation.
 */
public class InstallerConfiguration {
    private static final Logger log = Logger.getLogger(AgentInstaller.class);

    // these are standalone command line options that are *not* found in the config .properties file
    static final String OPTION_INSTALLER_CONFIG = "installer-config";
    static final String OPTION_ENCRYPTION_KEY = "encryption-key";
    static final String OPTION_ENCRYPTION_SALT = "encryption-salt";

    // these are command line options that can also be defined in the config .properties file
    static final String OPTION_ENABLED = "enabled";
    static final String OPTION_TARGET_LOCATION = "target-location";
    static final String OPTION_MODULE_DISTRIBUTION = "module-dist";
    static final String OPTION_TARGET_CONFIG = "target-config";
    static final String OPTION_SUBSYSTEM_SNIPPET = "subsystem-snippet";
    static final String OPTION_SERVER_URL = "server-url";
    static final String OPTION_KEYSTORE_PATH = "keystore-path";
    static final String OPTION_KEYSTORE_PASSWORD = "keystore-password";
    static final String OPTION_KEY_PASSWORD = "key-password";
    static final String OPTION_KEY_ALIAS = "key-alias";
    static final String OPTION_USERNAME = "username";
    static final String OPTION_PASSWORD = "password";
    static final String OPTION_SECURITY_KEY = "security-key";
    static final String OPTION_SECURITY_SECRET = "security-secret";
    static final String OPTION_MANAGED_SERVER_NAME = "managed-server-name";
    static final String OPTION_FEED_ID = "feed-id";

    static Options buildCommandLineOptions() {
        Options options = new Options();

        options.addOption(Option.builder("D")
                .hasArgs()
                .valueSeparator('=')
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_INSTALLER_CONFIG)
                .longOpt(InstallerConfiguration.OPTION_INSTALLER_CONFIG)
                .desc("Installer .properties configuration file")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_ENCRYPTION_KEY)
                .longOpt(InstallerConfiguration.OPTION_ENCRYPTION_KEY)
                .desc("If specified, this is used to decode the properties that were encrypted. If you do not " +
                        "provide a value with the option, you will be prompted for one.")
                .numberOfArgs(1)
                .optionalArg(true) // if no argument is given, we'll ask on stdin for it
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_ENCRYPTION_SALT)
                .longOpt(InstallerConfiguration.OPTION_ENCRYPTION_SALT)
                .desc("The salt used for generating the key. Recommended, if encryption is used. If not specified, " +
                        "the same value as the key will be used.")
                .numberOfArgs(1)
                .optionalArg(true) // if no argument is given, we'll ask on stdin for it
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_ENABLED)
                .longOpt(InstallerConfiguration.OPTION_ENABLED)
                .desc("Indicates if the agent should be enabled at startup")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_TARGET_LOCATION)
                .longOpt(InstallerConfiguration.OPTION_TARGET_LOCATION)
                .desc("Target home directory of the application server where the agent is to be installed")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_MODULE_DISTRIBUTION)
                .longOpt(InstallerConfiguration.OPTION_MODULE_DISTRIBUTION)
                .desc("Hawkular WildFly Agent Module distribution zip file")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_SERVER_URL)
                .longOpt(InstallerConfiguration.OPTION_SERVER_URL)
                .desc("Server URL where the agent will send its monitoring data")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_TARGET_CONFIG)
                .longOpt(InstallerConfiguration.OPTION_TARGET_CONFIG)
                .desc("The target configuration file to write to. Can be either absolute path or relative to "
                        + InstallerConfiguration.OPTION_TARGET_LOCATION)
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_SUBSYSTEM_SNIPPET)
                .longOpt(InstallerConfiguration.OPTION_SUBSYSTEM_SNIPPET)
                .desc("Customized subsystem XML content that overrides the default subsystem configuration")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_MANAGED_SERVER_NAME)
                .longOpt(InstallerConfiguration.OPTION_MANAGED_SERVER_NAME)
                .desc("The agent will use this name to refer to the server where it is deployed and locally managing.")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_FEED_ID)
                .longOpt(InstallerConfiguration.OPTION_FEED_ID)
                .desc("The feed ID that the agent will use to identify its data.")
                .numberOfArgs(1)
                .build());

        // SSL/security related config options
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_KEYSTORE_PATH)
                .longOpt(InstallerConfiguration.OPTION_KEYSTORE_PATH)
                .desc("Keystore file. Required when " + InstallerConfiguration.OPTION_SERVER_URL
                        + " protocol is https")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_KEYSTORE_PASSWORD)
                .longOpt(InstallerConfiguration.OPTION_KEYSTORE_PASSWORD)
                .desc("Keystore password. When " + InstallerConfiguration.OPTION_SERVER_URL
                        + " protocol is https and this option is not passed, installer will ask for password")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_KEY_PASSWORD)
                .longOpt(InstallerConfiguration.OPTION_KEY_PASSWORD)
                .desc("Key password. When " + InstallerConfiguration.OPTION_SERVER_URL
                        + " protocol is https and this option is not passed, installer will ask for password")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_KEY_ALIAS)
                .longOpt(InstallerConfiguration.OPTION_KEY_ALIAS)
                .desc("Key alias. Required when " + InstallerConfiguration.OPTION_SERVER_URL
                        + " protocol is https")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_USERNAME)
                .longOpt(InstallerConfiguration.OPTION_USERNAME)
                .desc("User the agent will use when connecting to Hawkular Server. Ignored if " + OPTION_SECURITY_KEY
                        + " is provided.")
                .numberOfArgs(1)
                .build());
        options.addOption(Option
                .builder()
                .argName(InstallerConfiguration.OPTION_PASSWORD)
                .longOpt(InstallerConfiguration.OPTION_PASSWORD)
                .desc("Credentials agent will use when connecting to Hawkular Server. Ignored if "
                        + OPTION_SECURITY_KEY + " is provided.")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_SECURITY_KEY)
                .longOpt(InstallerConfiguration.OPTION_SECURITY_KEY)
                .desc("Security key that the agent will use when authenticating with Hawkular Server.")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder()
                .argName(InstallerConfiguration.OPTION_SECURITY_SECRET)
                .longOpt(InstallerConfiguration.OPTION_SECURITY_SECRET)
                .desc("Security secret that the agent will use when authenticating with Hawkular Server.")
                .numberOfArgs(1)
                .build());

        return options;
    }

    private final Properties properties;

    public InstallerConfiguration(CommandLine commandLine) throws Exception {
        this.properties = new Properties();

        // we allow the user to set system properties through the -D argument
        Properties sysprops = commandLine.getOptionProperties("D");
        for (Map.Entry<Object, Object> sysprop : sysprops.entrySet()) {
            System.setProperty(sysprop.getKey().toString(), sysprop.getValue().toString());
        }

        // seed our properties with the installer config properties file
        String installerConfig = commandLine.getOptionValue(OPTION_INSTALLER_CONFIG,
                "classpath:/hawkular-wildfly-agent-installer.properties");

        log.debug("Installer configuration file: " + installerConfig);

        if (installerConfig.startsWith("classpath:")) {
            installerConfig = installerConfig.substring(10);
            if (!installerConfig.startsWith("/")) {
                installerConfig = "/" + installerConfig;
            }
            properties.load(InstallerConfiguration.class.getResourceAsStream(installerConfig));
        } else if (installerConfig.matches("(http|https|file):.*")) {
            URL installerConfigUrl = new URL(installerConfig);
            try (InputStream is = installerConfigUrl.openStream()) {
                properties.load(is);
            }
        } else {
            File installerConfigFile = new File(installerConfig);
            try (FileInputStream fis = new FileInputStream(installerConfigFile)) {
                properties.load(fis);
            }
        }

        // now we override the defaults with options the user provided us
        setProperty(properties, commandLine, OPTION_ENABLED);
        setProperty(properties, commandLine, OPTION_TARGET_LOCATION);
        setProperty(properties, commandLine, OPTION_MODULE_DISTRIBUTION);
        setProperty(properties, commandLine, OPTION_TARGET_CONFIG);
        setProperty(properties, commandLine, OPTION_SUBSYSTEM_SNIPPET);
        setProperty(properties, commandLine, OPTION_MANAGED_SERVER_NAME);
        setProperty(properties, commandLine, OPTION_FEED_ID);
        setProperty(properties, commandLine, OPTION_SERVER_URL);
        setProperty(properties, commandLine, OPTION_KEYSTORE_PATH);
        setProperty(properties, commandLine, OPTION_KEYSTORE_PASSWORD);
        setProperty(properties, commandLine, OPTION_KEY_PASSWORD);
        setProperty(properties, commandLine, OPTION_KEY_ALIAS);
        setProperty(properties, commandLine, OPTION_USERNAME);
        setProperty(properties, commandLine, OPTION_PASSWORD);
        setProperty(properties, commandLine, OPTION_SECURITY_KEY);
        setProperty(properties, commandLine, OPTION_SECURITY_SECRET);
    }

    private void setProperty(Properties props, CommandLine commandLine, String option) {
        String value = commandLine.getOptionValue(option);
        if (value != null) {
            properties.setProperty(option, value);
        }
    }

    public void decodeProperties(String encryptionKey, byte[] salt) throws Exception {
        decodeProperty(properties, OPTION_KEYSTORE_PASSWORD, encryptionKey, salt);
        decodeProperty(properties, OPTION_KEY_PASSWORD, encryptionKey, salt);
        decodeProperty(properties, OPTION_PASSWORD, encryptionKey, salt);
        decodeProperty(properties, OPTION_SECURITY_SECRET, encryptionKey, salt);
    }

    private void decodeProperty(Properties prop, String option, String key, byte[] salt) throws Exception {
        String value = properties.getProperty(option, null);
        if (value != null) {
            value = EncoderDecoder.decode(value, key, salt);
            properties.setProperty(option, value);
        }
    }

    public String getInstallerConfig() {
        return properties.getProperty(OPTION_INSTALLER_CONFIG);
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(properties.getProperty(OPTION_ENABLED, "true"));
    }

    public String getTargetLocation() {
        return properties.getProperty(OPTION_TARGET_LOCATION);
    }

    public String getModuleDistribution() {
        return properties.getProperty(OPTION_MODULE_DISTRIBUTION);
    }

    public String getTargetConfig() {
        return properties.getProperty(OPTION_TARGET_CONFIG);
    }

    public String getSubsystemSnippet() {
        return properties.getProperty(OPTION_SUBSYSTEM_SNIPPET);
    }

    public String getServerUrl() {
        return properties.getProperty(OPTION_SERVER_URL);
    }

    public String getKeystorePath() {
        return properties.getProperty(OPTION_KEYSTORE_PATH);
    }

    public String getKeystorePassword() {
        return properties.getProperty(OPTION_KEYSTORE_PASSWORD);
    }

    public String getKeyPassword() {
        return properties.getProperty(OPTION_KEY_PASSWORD);
    }

    public String getKeyAlias() {
        return properties.getProperty(OPTION_KEY_ALIAS);
    }

    public String getUsername() {
        return properties.getProperty(OPTION_USERNAME);
    }

    public String getPassword() {
        return properties.getProperty(OPTION_PASSWORD);
    }

    public String getSecurityKey() {
        return properties.getProperty(OPTION_SECURITY_KEY);
    }

    public String getSecuritySecret() {
        return properties.getProperty(OPTION_SECURITY_SECRET);
    }

    public String getManagedServerName() {
        return properties.getProperty(OPTION_MANAGED_SERVER_NAME);
    }

    public String getFeedId() {
        return properties.getProperty(OPTION_FEED_ID);
    }
}
