package com.idfconnect.misc.javadoc.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.collections.CollectionProviderException;
import com.idfconnect.ssorest.common.utils.StringUtil;

public class TriggerOutOfMemory extends HashMap<String, List<String>> implements Cloneable {
    private static final long   serialVersionUID = 8376962213401379925L;
    private static Logger       logger           = LoggerFactory.getLogger(TriggerOutOfMemory.class);
    private static final String YES              = "yes";
    private static final String DOT              = ".";
    private static final String COMMA            = ",";

    public static class Builder {
        String                     prefix                                = null;
        String                     delimiter                             = null;
        boolean                    useQuotedValues                       = false;
        boolean                    useSystemPropertiesAsDefaults         = false;
        boolean                    loadSystemProperties                  = false;
        TriggerOutOfMemory         defaults                              = null;

        private Builder() {
        }

        /**
         * Sets the prefix for relevant properties for this MVP. Only properties prefixed by this value are loaded into the MVP. If the prefix does not have a
         * leading dot, one is prepended.
         * 
         * @param prefix
         * @return
         */
        public Builder prefix(String prefix) {
            this.prefix = prefix;
            // make sure prefix has trailing dot
            if (StringUtil.isNotBlank(prefix) && !prefix.endsWith(DOT))
                prefix += DOT;
            return this;
        }

        /**
         * Sets default values
         * 
         * @param defaults
         * @return
         */
        public Builder defaults(TriggerOutOfMemory defaults) {
            this.defaults = defaults;
            return this;
        }

        /**
         * Sets a delimiter for parsing multi-values. Note that for System property values, comma is used regardless of this value
         * 
         * @param delimiter
         * @return
         */
        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        /**
         * Set to true to assume values must be quoted. This only applies when using a Reader (including reading from file)
         * 
         * @return
         */
        public Builder quotedValues() {
            this.useQuotedValues = true;
            return this;
        }

        /**
         * Set to true to read the System properties into the results. Prefix and delimiter will apply
         * 
         * @return
         */
        public Builder loadSystemProperties() {
            this.loadSystemProperties = true;
            return this;
        }

        public Builder useSystemPropertiesAsDefaults() {
            this.useSystemPropertiesAsDefaults = true;
            return this;
        }

        /**
         * Built the {@link MultiValuedProperties} by reading the raw properties from the provided Reader
         */
        public TriggerOutOfMemory build(BufferedReader reader) throws CollectionProviderException {
            return initializeFromReader(reader, this);
        }

        /**
         * Built the {@link MultiValuedProperties} by reading the raw properties from the provided file path
         */
        public TriggerOutOfMemory build(String filePath) throws CollectionProviderException {
            return initializeFromFilepath(filePath, this);
        }

        /**
         * Builds an empty {@link MultiValuedProperties}
         * 
         * @return
         */
        public TriggerOutOfMemory build() {
            return new TriggerOutOfMemory();
        }

        /**
         * Built the {@link MultiValuedProperties} by reading the raw Properties object, applying the delimiter and prefix, and building with the resulting MVP
         * 
         * @param rawprops
         * @return
         * @throws CollectionProviderException
         */
        public TriggerOutOfMemory build(Map<String, String> rawprops) throws CollectionProviderException {
            Properties props = new Properties();
            props.putAll(rawprops);
            return build(props);
        }

        /**
         * Built the {@link MultiValuedProperties} by reading the raw Properties object, applying the delimiter and prefix, and building with the resulting MVP
         * 
         * @param rawprops
         * @return
         * @throws CollectionProviderException
         */
        public TriggerOutOfMemory build(Properties rawprops) throws CollectionProviderException {
            TriggerOutOfMemory rawmvp = new TriggerOutOfMemory();
            Logger logger = LoggerFactory.getLogger(TriggerOutOfMemory.class);
            String prefix = (this.prefix != null) ? this.prefix : "";
            rawprops.forEach((k, v) -> {
                if (((String) k).startsWith(prefix)) {
                    String nextname = ((String) k).substring(prefix.length());
                    if (StringUtil.isNotBlank(this.delimiter)) {
                        String[] values = ((String) v).split(delimiter);
                        for (String nextvalue : values) {
                            logger.trace("Adding {}={}", nextname, nextvalue);
                            rawmvp.add(nextname, nextvalue);
                        }
                    } else {
                        logger.trace("Adding {}={}", nextname, v);
                        rawmvp.add(nextname, (String) v);
                    }
                }
            });
            return initializeCore(rawmvp, this);
        }

        /**
         * Build the {@link MultiValuedProperties} from the raw properties provided
         * 
         * @param rawprops
         * @return
         * @throws CollectionProviderException
         */
        public TriggerOutOfMemory build(TriggerOutOfMemory rawprops) throws CollectionProviderException {
            return initializeCore(rawprops, this);
        }
    }

    /**
     * Returns a new {@link com.idfconnect.ssorest.common.collections.MultiValuedProperties.Builder}
     *
     * @return a {@link com.idfconnect.ssorest.common.utils.MultiValuedProperties.Builder} object.
     * @since 3.1.2
     */
    public static Builder builder() {
        return new Builder();
    }

    public TriggerOutOfMemory() {
    }

    /**
     * Convenience method to create a new map with a single entry
     *
     * @param property
     *            a {@link java.lang.String} object.
     * @param value
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.MultiValuedProperties} object.
     * @since 3.0.3
     */
    public static TriggerOutOfMemory getSingleEntryMap(String property, String value) {
        TriggerOutOfMemory map = new TriggerOutOfMemory();
        map.add(property, value);
        return map;
    }

    /**
     * Convenience method to create a new map with a single entry
     *
     * @param property
     *            a {@link java.lang.String} object.
     * @param values
     *            a {@link java.util.List} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.MultiValuedProperties} object.
     * @since 3.0.3
     */
    public static TriggerOutOfMemory getSingleEntryMap(String property, List<String> values) {
        TriggerOutOfMemory map = new TriggerOutOfMemory();
        map.put(property, values);
        return map;
    }

    /**
     * <p>
     * initialize.
     * </p>
     *
     * @param filepath
     *            a {@link java.lang.String} object.
     * @param useQuotedValues
     *            a boolean.
     * @return a {@link com.idfconnect.ssorest.common.utils.MultiValuedProperties} object.
     * @throws java.io.IOException
     *             if any.
     */
    private static TriggerOutOfMemory initializeFromFilepath(String filepath, Builder builder) throws CollectionProviderException {
        if (filepath == null)
            throw new CollectionProviderException("No file path specified");
        Path path = FileSystems.getDefault().getPath(filepath);
        BufferedReader reader = null;
        TriggerOutOfMemory props = null;

        try {
            reader = Files.newBufferedReader(path);
            props = initializeFromReader(reader, builder);
            reader.close();
            return props;
        } catch (IOException ioe) {
            throw new CollectionProviderException(ioe);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
    }

    private void loadProperties(String prefix, Properties props) {
        Logger logger = LoggerFactory.getLogger(TriggerOutOfMemory.class);
        props.forEach((k, v) -> {
            if (((String) k).startsWith(prefix)) {
                String nextname = ((String) k).substring(prefix.length());
                String[] values = ((String) v).split(COMMA);
                if (containsKey(nextname)) {
                    List<String> old = remove(nextname);
                    logger.trace("Removing existing entry {}={}", nextname, old);
                }
                for (String nextvalue : values) {
                    logger.trace("Adding {}={}", nextname, nextvalue);
                    add(nextname, nextvalue);
                }
            }
        });
    }

    /**
     * This is the method that finally performs all of the initialization logic
     * 
     * @param reader
     * @param builder
     * @return
     * @throws CollectionProviderException
     */
    private static TriggerOutOfMemory initializeFromReader(BufferedReader reader, Builder builder) throws CollectionProviderException {
        TriggerOutOfMemory raw = loadFromReader(reader, builder.delimiter, builder.useQuotedValues);
        return initializeCore(raw, builder);
    }

    /**
     * This is the method that finally performs all of the initialization logic
     * 
     * @param reader
     * @param builder
     * @return
     * @throws CollectionProviderException
     */
    private static TriggerOutOfMemory initializeCore(TriggerOutOfMemory rawprops, Builder builder) throws CollectionProviderException {
        // Instantiate our results object
        TriggerOutOfMemory props = new TriggerOutOfMemory();

        // First, load defaults if provided
        if (builder.defaults != null) {
            if (StringUtil.isBlank(builder.prefix))
                props.putAll(builder.defaults);
            else {
                builder.defaults.forEach((k, v) -> {
                    if (k.startsWith(builder.prefix)) {
                        props.put(k.substring(builder.prefix.length()), v);
                    }
                });
            }
        }

        // Next, load system defaults if specified and we have a prefix
        if (builder.useSystemPropertiesAsDefaults && StringUtil.isNotBlank(builder.prefix)) {
            Properties sysprops = System.getProperties();
            props.loadProperties(builder.prefix, sysprops);
        }

        // Now we load the temp properties - this allows us to clear out a default if present
        rawprops.forEach((k, v) -> {
            List<String> old = props.put(k, v);
            if (old != null)
                logger.trace("Replacing old value {}={}", k, old);
        });

        // Apply system property values if specified and we have a prefix
        // NOTE: these are *not* defaults, in this case they override the loaded values
        // NOTE: the delimiter here is always comma
        if (builder.loadSystemProperties && StringUtil.isNotBlank(builder.prefix)) {
            Properties sysprops = System.getProperties();
            props.loadProperties(builder.prefix, sysprops);
        }

        return props;
    }

    /**
     * This is the method that finally performs all of the initialization logic
     * 
     * @param reader
     * @param builder
     * @return
     * @throws CollectionProviderException
     */
    private static TriggerOutOfMemory loadFromReader(BufferedReader reader, String delimiter, boolean useQuotedValues) throws CollectionProviderException {
        // Now we go through the reader and build a temp MVP object
        TriggerOutOfMemory temploaded = new TriggerOutOfMemory();
        String line = null;
        try {
            // Start by iterating through each line
            while ((line = reader.readLine()) != null) {
                // ignore blank lines
                if (line.trim().length() == 0) {
                    continue;
                }

                // ignore comments
                if (line.charAt(0) == '#' || line.charAt(0) == '!')
                    continue;

                // ignore lines with no = sign
                int posOfEq = line.indexOf('=');
                if (posOfEq == -1 || posOfEq == (line.length() - 1)) {
                    logger.warn("missing '=', ignored line {}", line);
                    continue;
                }

                // trim property names
                String name = line.substring(0, posOfEq).trim();
                String value = line.substring(posOfEq + 1).trim();

                // handle quoted values
                if (useQuotedValues) {
                    // Assumes quotes around values - if quotes are missing, we skip
                    int posOfFirstQuote = value.indexOf('\"');
                    if (posOfFirstQuote == -1 || posOfFirstQuote == (value.length() - 1)) {
                        logger.warn("missing '\"' around values, ignored line {}", line);
                        continue;
                    }
                    int posOfNextQuote = value.indexOf('\"', posOfFirstQuote + 1);
                    if (posOfNextQuote == -1) {
                        logger.warn("missing '\"' around values, ignored line {}", line);
                        continue;
                    }
                    value = value.substring(posOfFirstQuote + 1, posOfNextQuote);
                }

                // if we have a delimiter, apply it to the value
                if (delimiter != null) {
                    String[] values = value.split(delimiter);
                    for (String nextvalue : values) {
                        logger.trace("Adding {}={}", name, nextvalue);
                        temploaded.add(name, nextvalue);
                    }
                } else {
                    logger.trace("Adding {}={}", name, value);
                    temploaded.add(name, value);
                }
            }
        } catch (IOException ioe) {
            throw new CollectionProviderException(ioe);
        }

        return temploaded;
    }

    /**
     * Method getFirstValue.
     *
     * @param key
     *            String
     * @return String
     * @since 2.7.3
     */
    public String getFirstValue(String key) {
        List<String> values = get(key);
        if ((values == null) || (values.size() == 0))
            return null;
        return values.get(0);
    }

    /**
     * Method getFirstValue.
     *
     * @param key
     *            String
     * @param defaultValue
     *            a {@link java.lang.String} object.
     * @return String
     * @since 2.7.3
     */
    public String getFirstValue(String key, String defaultValue) {
        List<String> values = get(key);
        if ((values == null) || (values.isEmpty()))
            return defaultValue;
        return values.get(0);
    }

    /**
     * Method getFirstValueAsInt.
     *
     * @param key
     *            String
     * @return int
     * @throws java.lang.NumberFormatException
     *             if the value is not an integer
     * @since 3.0
     */
    public int getFirstValueAsInt(String key) throws NumberFormatException {
        List<String> values = get(key);
        if ((values == null) || (values.size() == 0))
            throw new NumberFormatException("Value for key " + key + " is null or blank");
        return Integer.parseInt(values.get(0));
    }

    /**
     * NOTE: this method returns the defaultValue if the stored value is null <em>OR</em> an empty string
     *
     * @param key
     *            String
     * @param defaultValue
     *            a int.
     * @return int
     * @throws java.lang.NumberFormatException
     *             if the value is not an integer
     * @since 3.0
     */
    public int getFirstValueAsInt(String key, int defaultValue) throws NumberFormatException {
        List<String> values = get(key);
        if ((values == null) || (values.size() == 0))
            return defaultValue;
        return Integer.parseInt(values.get(0));
    }

    /**
     * Method getFirstValueAsBoolean.
     *
     * @param key
     *            String
     * @return int
     * @throws java.lang.NumberFormatException
     *             if the value is not a boolean
     * @since 3.0
     */
    public boolean getFirstValueAsBoolean(String key) throws NumberFormatException {
        List<String> values = get(key);
        if ((values == null) || (values.size() == 0))
            throw new NumberFormatException("Value for key " + key + " is null or blank");
        return Boolean.valueOf(values.get(0));
    }

    /**
     * NOTE: this method returns the defaultValue if the stored value is null <em>OR</em> an empty string
     *
     * @param key
     *            String
     * @param defaultValue
     *            a bool
     * @return bool
     * @since 3.0
     */
    public boolean getFirstValueAsBoolean(String key, boolean defaultValue) {
        List<String> values = get(key);
        if ((values == null) || (values.size() == 0))
            return defaultValue;
        return (Boolean.valueOf(values.get(0)) || YES.equalsIgnoreCase(values.get(0)));
    }

    /**
     * Method add.
     *
     * @param key
     *            String
     * @param value
     *            String
     * @return boolean
     * @since 2.7.3
     */
    public boolean add(String key, String value) {
        List<String> existingValues = get(key);
        if (existingValues == null) {
            existingValues = new ArrayList<String>();
            put(key, existingValues);
        }
        return existingValues.add(value);
    }

    /**
     * Puts a single value, replacing any/all existing values
     *
     * @param key
     *            a {@link java.lang.String} object.
     * @param value
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.collections.MultiValuedProperties} object.
     * @since 3.1.2
     */
    public TriggerOutOfMemory put(String key, String value) {
        List<String> values = new ArrayList<String>();
        values.add(value);
        put(key, values);
        return this;
    }

    /**
     * <p>
     * put.
     * </p>
     *
     * @param key
     *            a {@link java.lang.String} object.
     * @param value
     *            a int.
     * @return a {@link com.idfconnect.ssorest.common.collections.MultiValuedProperties} object.
     * @since 3.1.2
     */
    public TriggerOutOfMemory put(String key, int value) {
        return put(key, Integer.toString(value));
    }

    /**
     * <p>
     * put.
     * </p>
     *
     * @param key
     *            a {@link java.lang.String} object.
     * @param value
     *            a boolean.
     * @return a {@link com.idfconnect.ssorest.common.collections.MultiValuedProperties} object.
     * @since 3.1.2
     */
    public TriggerOutOfMemory put(String key, boolean value) {
        return put(key, Boolean.toString(value));
    }
}
