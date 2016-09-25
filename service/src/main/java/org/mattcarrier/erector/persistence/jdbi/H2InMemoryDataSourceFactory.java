/**
 * The MIT License
 * Copyright © 2016 Matt Carrier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.mattcarrier.erector.persistence.jdbi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.Ints;

import io.dropwizard.db.DataSourceFactory.TransactionIsolation;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.ManagedPooledDataSource;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;
import io.dropwizard.validation.ValidationMethod;

public class H2InMemoryDataSourceFactory implements ErectorDataSourceFactory {
    @NotNull
    private String driverClass = "org.h2.Driver";

    @Min(0)
    @Max(100)
    private int abandonWhenPercentageFull = 0;

    private boolean alternateUsernamesAllowed = false;

    private boolean commitOnReturn = false;

    private boolean rollbackOnReturn = false;

    private Boolean autoCommitByDefault;

    private Boolean readOnlyByDefault;

    private String user = "sa";

    private String password = null;

    @NotNull
    private String url = "jdbc:h2:mem:JDBITest-" + System.currentTimeMillis();

    @NotNull
    private Map<String, String> properties = new LinkedHashMap<>();

    private String defaultCatalog;

    @NotNull
    private TransactionIsolation defaultTransactionIsolation = TransactionIsolation.DEFAULT;

    private boolean useFairQueue = true;

    @Min(0)
    private int initialSize = 10;

    @Min(0)
    private int minSize = 10;

    @Min(1)
    private int maxSize = 100;

    private String initializationQuery;

    private boolean logAbandonedConnections = false;

    private boolean logValidationErrors = false;

    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    private Duration maxConnectionAge;

    @NotNull
    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    private Duration maxWaitForConnection = Duration.seconds(30);

    @NotNull
    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    private Duration minIdleTime = Duration.minutes(1);

    @NotNull
    private String validationQuery = "/* Health Check */ SELECT 1";

    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    private Duration validationQueryTimeout;

    private boolean checkConnectionWhileIdle = true;

    private boolean checkConnectionOnBorrow = false;

    private boolean checkConnectionOnConnect = true;

    private boolean checkConnectionOnReturn = false;

    private boolean autoCommentsEnabled = true;

    @NotNull
    @MinDuration(1)
    private Duration evictionInterval = Duration.seconds(5);

    @NotNull
    @MinDuration(1)
    private Duration validationInterval = Duration.seconds(30);

    private Optional<String> validatorClassName = Optional.empty();

    private boolean removeAbandoned = false;

    @NotNull
    @MinDuration(1)
    private Duration removeAbandonedTimeout = Duration.seconds(60L);

    @JsonProperty
    @Override
    public boolean isAutoCommentsEnabled() {
        return autoCommentsEnabled;
    }

    @JsonProperty
    public void setAutoCommentsEnabled(boolean autoCommentsEnabled) {
        this.autoCommentsEnabled = autoCommentsEnabled;
    }

    @JsonProperty
    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @JsonProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @JsonProperty
    public String getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    @Override
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonProperty
    public Duration getMaxWaitForConnection() {
        return maxWaitForConnection;
    }

    @JsonProperty
    public void setMaxWaitForConnection(Duration maxWaitForConnection) {
        this.maxWaitForConnection = maxWaitForConnection;
    }

    @Override
    @JsonProperty
    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    @Deprecated
    @JsonIgnore
    public String getHealthCheckValidationQuery() {
        return getValidationQuery();
    }

    @JsonProperty
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    @JsonProperty
    public int getMinSize() {
        return minSize;
    }

    @JsonProperty
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    @JsonProperty
    public int getMaxSize() {
        return maxSize;
    }

    @JsonProperty
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @JsonProperty
    public boolean getCheckConnectionWhileIdle() {
        return checkConnectionWhileIdle;
    }

    @JsonProperty
    public void setCheckConnectionWhileIdle(boolean checkConnectionWhileIdle) {
        this.checkConnectionWhileIdle = checkConnectionWhileIdle;
    }

    @Deprecated
    @JsonProperty
    public boolean isDefaultReadOnly() {
        return Boolean.TRUE.equals(readOnlyByDefault);
    }

    @Deprecated
    @JsonProperty
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        readOnlyByDefault = defaultReadOnly;
    }

    @JsonIgnore
    @ValidationMethod(message = ".minSize must be less than or equal to maxSize")
    public boolean isMinSizeLessThanMaxSize() {
        return minSize <= maxSize;
    }

    @JsonIgnore
    @ValidationMethod(message = ".initialSize must be less than or equal to maxSize")
    public boolean isInitialSizeLessThanMaxSize() {
        return initialSize <= maxSize;
    }

    @JsonIgnore
    @ValidationMethod(message = ".initialSize must be greater than or equal to minSize")
    public boolean isInitialSizeGreaterThanMinSize() {
        return minSize <= initialSize;
    }

    @JsonProperty
    public int getAbandonWhenPercentageFull() {
        return abandonWhenPercentageFull;
    }

    @JsonProperty
    public void setAbandonWhenPercentageFull(int percentage) {
        this.abandonWhenPercentageFull = percentage;
    }

    @JsonProperty
    public boolean isAlternateUsernamesAllowed() {
        return alternateUsernamesAllowed;
    }

    @JsonProperty
    public void setAlternateUsernamesAllowed(boolean allow) {
        this.alternateUsernamesAllowed = allow;
    }

    @JsonProperty
    public boolean getCommitOnReturn() {
        return commitOnReturn;
    }

    @JsonProperty
    public boolean getRollbackOnReturn() {
        return rollbackOnReturn;
    }

    @JsonProperty
    public void setCommitOnReturn(boolean commitOnReturn) {
        this.commitOnReturn = commitOnReturn;
    }

    @JsonProperty
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    @JsonProperty
    public Boolean getAutoCommitByDefault() {
        return autoCommitByDefault;
    }

    @JsonProperty
    public void setAutoCommitByDefault(Boolean autoCommit) {
        this.autoCommitByDefault = autoCommit;
    }

    @JsonProperty
    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    @JsonProperty
    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    @JsonProperty
    public Boolean getReadOnlyByDefault() {
        return readOnlyByDefault;
    }

    @JsonProperty
    public void setReadOnlyByDefault(Boolean readOnlyByDefault) {
        this.readOnlyByDefault = readOnlyByDefault;
    }

    @JsonProperty
    public TransactionIsolation getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    @JsonProperty
    public void setDefaultTransactionIsolation(TransactionIsolation isolation) {
        this.defaultTransactionIsolation = isolation;
    }

    @JsonProperty
    public boolean getUseFairQueue() {
        return useFairQueue;
    }

    @JsonProperty
    public void setUseFairQueue(boolean fair) {
        this.useFairQueue = fair;
    }

    @JsonProperty
    public int getInitialSize() {
        return initialSize;
    }

    @JsonProperty
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    @JsonProperty
    public String getInitializationQuery() {
        return initializationQuery;
    }

    @JsonProperty
    public void setInitializationQuery(String query) {
        this.initializationQuery = query;
    }

    @JsonProperty
    public boolean getLogAbandonedConnections() {
        return logAbandonedConnections;
    }

    @JsonProperty
    public void setLogAbandonedConnections(boolean log) {
        this.logAbandonedConnections = log;
    }

    @JsonProperty
    public boolean getLogValidationErrors() {
        return logValidationErrors;
    }

    @JsonProperty
    public void setLogValidationErrors(boolean log) {
        this.logValidationErrors = log;
    }

    @JsonProperty
    public Optional<Duration> getMaxConnectionAge() {
        return Optional.ofNullable(maxConnectionAge);
    }

    @JsonProperty
    public void setMaxConnectionAge(Duration age) {
        this.maxConnectionAge = age;
    }

    @JsonProperty
    public Duration getMinIdleTime() {
        return minIdleTime;
    }

    @JsonProperty
    public void setMinIdleTime(Duration time) {
        this.minIdleTime = time;
    }

    @JsonProperty
    public boolean getCheckConnectionOnBorrow() {
        return checkConnectionOnBorrow;
    }

    @JsonProperty
    public void setCheckConnectionOnBorrow(boolean checkConnectionOnBorrow) {
        this.checkConnectionOnBorrow = checkConnectionOnBorrow;
    }

    @JsonProperty
    public boolean getCheckConnectionOnConnect() {
        return checkConnectionOnConnect;
    }

    @JsonProperty
    public void setCheckConnectionOnConnect(boolean checkConnectionOnConnect) {
        this.checkConnectionOnConnect = checkConnectionOnConnect;
    }

    @JsonProperty
    public boolean getCheckConnectionOnReturn() {
        return checkConnectionOnReturn;
    }

    @JsonProperty
    public void setCheckConnectionOnReturn(boolean checkConnectionOnReturn) {
        this.checkConnectionOnReturn = checkConnectionOnReturn;
    }

    @JsonProperty
    public Duration getEvictionInterval() {
        return evictionInterval;
    }

    @JsonProperty
    public void setEvictionInterval(Duration interval) {
        this.evictionInterval = interval;
    }

    @JsonProperty
    public Duration getValidationInterval() {
        return validationInterval;
    }

    @JsonProperty
    public void setValidationInterval(Duration validationInterval) {
        this.validationInterval = validationInterval;
    }

    @Override
    @JsonProperty
    public Optional<Duration> getValidationQueryTimeout() {
        return Optional.ofNullable(validationQueryTimeout);
    }

    @JsonProperty
    public Optional<String> getValidatorClassName() {
        return validatorClassName;
    }

    @JsonProperty
    public void setValidatorClassName(Optional<String> validatorClassName) {
        this.validatorClassName = validatorClassName;
    }

    @Override
    @Deprecated
    @JsonIgnore
    public Optional<Duration> getHealthCheckValidationTimeout() {
        return getValidationQueryTimeout();
    }

    @JsonProperty
    public void setValidationQueryTimeout(Duration validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    @JsonProperty
    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    @JsonProperty
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    @JsonProperty
    public Duration getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    @JsonProperty
    public void setRemoveAbandonedTimeout(Duration removeAbandonedTimeout) {
        this.removeAbandonedTimeout = Objects.requireNonNull(removeAbandonedTimeout);
    }

    @Override
    public void asSingleConnectionPool() {
        minSize = 1;
        maxSize = 1;
        initialSize = 1;
    }

    @Override
    public ManagedDataSource build(MetricRegistry metricRegistry, String name) {
        final Properties properties = new Properties();
        for (Map.Entry<String, String> property : this.properties.entrySet()) {
            properties.setProperty(property.getKey(), property.getValue());
        }

        final PoolProperties poolConfig = new PoolProperties();
        poolConfig.setAbandonWhenPercentageFull(abandonWhenPercentageFull);
        poolConfig.setAlternateUsernameAllowed(alternateUsernamesAllowed);
        poolConfig.setCommitOnReturn(commitOnReturn);
        poolConfig.setRollbackOnReturn(rollbackOnReturn);
        poolConfig.setDbProperties(properties);
        poolConfig.setDefaultAutoCommit(autoCommitByDefault);
        poolConfig.setDefaultCatalog(defaultCatalog);
        poolConfig.setDefaultReadOnly(readOnlyByDefault);
        poolConfig.setDefaultTransactionIsolation(defaultTransactionIsolation.get());
        poolConfig.setDriverClassName(driverClass);
        poolConfig.setFairQueue(useFairQueue);
        poolConfig.setInitialSize(initialSize);
        poolConfig.setInitSQL(initializationQuery);
        poolConfig.setLogAbandoned(logAbandonedConnections);
        poolConfig.setLogValidationErrors(logValidationErrors);
        poolConfig.setMaxActive(maxSize);
        poolConfig.setMaxIdle(maxSize);
        poolConfig.setMinIdle(minSize);

        if (getMaxConnectionAge().isPresent()) {
            poolConfig.setMaxAge(maxConnectionAge.toMilliseconds());
        }

        poolConfig.setMaxWait((int) maxWaitForConnection.toMilliseconds());
        poolConfig.setMinEvictableIdleTimeMillis((int) minIdleTime.toMilliseconds());
        poolConfig.setName(name);
        poolConfig.setUrl(url);
        poolConfig.setUsername(user);
        poolConfig.setPassword(user != null && password == null ? "" : password);
        poolConfig.setRemoveAbandoned(removeAbandoned);
        poolConfig.setRemoveAbandonedTimeout(Ints.saturatedCast(removeAbandonedTimeout.toSeconds()));

        poolConfig.setTestWhileIdle(checkConnectionWhileIdle);
        poolConfig.setValidationQuery(validationQuery);
        poolConfig.setTestOnBorrow(checkConnectionOnBorrow);
        poolConfig.setTestOnConnect(checkConnectionOnConnect);
        poolConfig.setTestOnReturn(checkConnectionOnReturn);
        poolConfig.setTimeBetweenEvictionRunsMillis((int) evictionInterval.toMilliseconds());
        poolConfig.setValidationInterval(validationInterval.toMilliseconds());

        if (getValidationQueryTimeout().isPresent()) {
            poolConfig.setValidationQueryTimeout((int) validationQueryTimeout.toSeconds());
        }
        if (validatorClassName.isPresent()) {
            poolConfig.setValidatorClassName(validatorClassName.get());
        }

        return new ManagedPooledDataSource(poolConfig, metricRegistry);
    }
}
