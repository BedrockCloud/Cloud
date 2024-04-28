package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final DnsjavaLibraryAccessors laccForDnsjavaLibraryAccessors = new DnsjavaLibraryAccessors(owner);
    private final ItLibraryAccessors laccForItLibraryAccessors = new ItLibraryAccessors(owner);
    private final JunitLibraryAccessors laccForJunitLibraryAccessors = new JunitLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>com</b>
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>dnsjava</b>
     */
    public DnsjavaLibraryAccessors getDnsjava() {
        return laccForDnsjavaLibraryAccessors;
    }

    /**
     * Group of libraries at <b>it</b>
     */
    public ItLibraryAccessors getIt() {
        return laccForItLibraryAccessors;
    }

    /**
     * Group of libraries at <b>junit</b>
     */
    public JunitLibraryAccessors getJunit() {
        return laccForJunitLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComGoogleLibraryAccessors laccForComGoogleLibraryAccessors = new ComGoogleLibraryAccessors(owner);
        private final ComGooglecodeLibraryAccessors laccForComGooglecodeLibraryAccessors = new ComGooglecodeLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google</b>
         */
        public ComGoogleLibraryAccessors getGoogle() {
            return laccForComGoogleLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.googlecode</b>
         */
        public ComGooglecodeLibraryAccessors getGooglecode() {
            return laccForComGooglecodeLibraryAccessors;
        }

    }

    public static class ComGoogleLibraryAccessors extends SubDependencyFactory {
        private final ComGoogleCodeLibraryAccessors$1 laccForComGoogleCodeLibraryAccessors$1 = new ComGoogleCodeLibraryAccessors$1(owner);
        private final ComGoogleGuavaLibraryAccessors laccForComGoogleGuavaLibraryAccessors = new ComGoogleGuavaLibraryAccessors(owner);

        public ComGoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google.code</b>
         */
        public ComGoogleCodeLibraryAccessors$1 getCode() {
            return laccForComGoogleCodeLibraryAccessors$1;
        }

        /**
         * Group of libraries at <b>com.google.guava</b>
         */
        public ComGoogleGuavaLibraryAccessors getGuava() {
            return laccForComGoogleGuavaLibraryAccessors;
        }

    }

    public static class ComGoogleCodeLibraryAccessors$1 extends SubDependencyFactory {
        private final ComGoogleCodeGsonLibraryAccessors laccForComGoogleCodeGsonLibraryAccessors = new ComGoogleCodeGsonLibraryAccessors(owner);

        public ComGoogleCodeLibraryAccessors$1(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google.code.gson</b>
         */
        public ComGoogleCodeGsonLibraryAccessors getGson() {
            return laccForComGoogleCodeGsonLibraryAccessors;
        }

    }

    public static class ComGoogleCodeGsonLibraryAccessors extends SubDependencyFactory {

        public ComGoogleCodeGsonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>gson</b> with <b>com.google.code.gson:gson</b> coordinates and
         * with version reference <b>com.google.code.gson.gson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("com.google.code.gson.gson");
        }

    }

    public static class ComGoogleGuavaLibraryAccessors extends SubDependencyFactory {

        public ComGoogleGuavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>guava</b> with <b>com.google.guava:guava</b> coordinates and
         * with version reference <b>com.google.guava.guava</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGuava() {
            return create("com.google.guava.guava");
        }

    }

    public static class ComGooglecodeLibraryAccessors extends SubDependencyFactory {
        private final ComGooglecodeJsonLibraryAccessors laccForComGooglecodeJsonLibraryAccessors = new ComGooglecodeJsonLibraryAccessors(owner);

        public ComGooglecodeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.googlecode.json</b>
         */
        public ComGooglecodeJsonLibraryAccessors getJson() {
            return laccForComGooglecodeJsonLibraryAccessors;
        }

    }

    public static class ComGooglecodeJsonLibraryAccessors extends SubDependencyFactory {
        private final ComGooglecodeJsonSimpleLibraryAccessors laccForComGooglecodeJsonSimpleLibraryAccessors = new ComGooglecodeJsonSimpleLibraryAccessors(owner);

        public ComGooglecodeJsonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.googlecode.json.simple</b>
         */
        public ComGooglecodeJsonSimpleLibraryAccessors getSimple() {
            return laccForComGooglecodeJsonSimpleLibraryAccessors;
        }

    }

    public static class ComGooglecodeJsonSimpleLibraryAccessors extends SubDependencyFactory {
        private final ComGooglecodeJsonSimpleJsonLibraryAccessors laccForComGooglecodeJsonSimpleJsonLibraryAccessors = new ComGooglecodeJsonSimpleJsonLibraryAccessors(owner);

        public ComGooglecodeJsonSimpleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.googlecode.json.simple.json</b>
         */
        public ComGooglecodeJsonSimpleJsonLibraryAccessors getJson() {
            return laccForComGooglecodeJsonSimpleJsonLibraryAccessors;
        }

    }

    public static class ComGooglecodeJsonSimpleJsonLibraryAccessors extends SubDependencyFactory {

        public ComGooglecodeJsonSimpleJsonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>simple</b> with <b>com.googlecode.json-simple:json-simple</b> coordinates and
         * with version reference <b>com.googlecode.json.simple.json.simple</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSimple() {
            return create("com.googlecode.json.simple.json.simple");
        }

    }

    public static class DnsjavaLibraryAccessors extends SubDependencyFactory {

        public DnsjavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>dnsjava</b> with <b>dnsjava:dnsjava</b> coordinates and
         * with version reference <b>dnsjava.dnsjava</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getDnsjava() {
            return create("dnsjava.dnsjava");
        }

    }

    public static class ItLibraryAccessors extends SubDependencyFactory {
        private final ItUnimiLibraryAccessors laccForItUnimiLibraryAccessors = new ItUnimiLibraryAccessors(owner);

        public ItLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>it.unimi</b>
         */
        public ItUnimiLibraryAccessors getUnimi() {
            return laccForItUnimiLibraryAccessors;
        }

    }

    public static class ItUnimiLibraryAccessors extends SubDependencyFactory {
        private final ItUnimiDsiLibraryAccessors laccForItUnimiDsiLibraryAccessors = new ItUnimiDsiLibraryAccessors(owner);

        public ItUnimiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>it.unimi.dsi</b>
         */
        public ItUnimiDsiLibraryAccessors getDsi() {
            return laccForItUnimiDsiLibraryAccessors;
        }

    }

    public static class ItUnimiDsiLibraryAccessors extends SubDependencyFactory {

        public ItUnimiDsiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>fastutil</b> with <b>it.unimi.dsi:fastutil</b> coordinates and
         * with version reference <b>it.unimi.dsi.fastutil</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getFastutil() {
            return create("it.unimi.dsi.fastutil");
        }

    }

    public static class JunitLibraryAccessors extends SubDependencyFactory {

        public JunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>junit</b> with <b>junit:junit</b> coordinates and
         * with version reference <b>junit.junit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() {
            return create("junit.junit");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgJetbrainsLibraryAccessors laccForOrgJetbrainsLibraryAccessors = new OrgJetbrainsLibraryAccessors(owner);
        private final OrgJlineLibraryAccessors laccForOrgJlineLibraryAccessors = new OrgJlineLibraryAccessors(owner);
        private final OrgOw2LibraryAccessors laccForOrgOw2LibraryAccessors = new OrgOw2LibraryAccessors(owner);
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSlf4jLibraryAccessors laccForOrgSlf4jLibraryAccessors = new OrgSlf4jLibraryAccessors(owner);
        private final OrgYamlLibraryAccessors laccForOrgYamlLibraryAccessors = new OrgYamlLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.jetbrains</b>
         */
        public OrgJetbrainsLibraryAccessors getJetbrains() {
            return laccForOrgJetbrainsLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.jline</b>
         */
        public OrgJlineLibraryAccessors getJline() {
            return laccForOrgJlineLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.ow2</b>
         */
        public OrgOw2LibraryAccessors getOw2() {
            return laccForOrgOw2LibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.projectlombok</b>
         */
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.slf4j</b>
         */
        public OrgSlf4jLibraryAccessors getSlf4j() {
            return laccForOrgSlf4jLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.yaml</b>
         */
        public OrgYamlLibraryAccessors getYaml() {
            return laccForOrgYamlLibraryAccessors;
        }

    }

    public static class OrgJetbrainsLibraryAccessors extends SubDependencyFactory {

        public OrgJetbrainsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>annotations</b> with <b>org.jetbrains:annotations</b> coordinates and
         * with version reference <b>org.jetbrains.annotations</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAnnotations() {
            return create("org.jetbrains.annotations");
        }

    }

    public static class OrgJlineLibraryAccessors extends SubDependencyFactory {
        private final OrgJlineJlineLibraryAccessors laccForOrgJlineJlineLibraryAccessors = new OrgJlineJlineLibraryAccessors(owner);

        public OrgJlineLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.jline.jline</b>
         */
        public OrgJlineJlineLibraryAccessors getJline() {
            return laccForOrgJlineJlineLibraryAccessors;
        }

    }

    public static class OrgJlineJlineLibraryAccessors extends SubDependencyFactory {

        public OrgJlineJlineLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>reader</b> with <b>org.jline:jline-reader</b> coordinates and
         * with version reference <b>org.jline.jline.reader</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getReader() {
            return create("org.jline.jline.reader");
        }

    }

    public static class OrgOw2LibraryAccessors extends SubDependencyFactory {
        private final OrgOw2AsmLibraryAccessors laccForOrgOw2AsmLibraryAccessors = new OrgOw2AsmLibraryAccessors(owner);

        public OrgOw2LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.ow2.asm</b>
         */
        public OrgOw2AsmLibraryAccessors getAsm() {
            return laccForOrgOw2AsmLibraryAccessors;
        }

    }

    public static class OrgOw2AsmLibraryAccessors extends SubDependencyFactory {

        public OrgOw2AsmLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>asm</b> with <b>org.ow2.asm:asm</b> coordinates and
         * with version reference <b>org.ow2.asm.asm</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAsm() {
            return create("org.ow2.asm.asm");
        }

    }

    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>org.projectlombok.lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLombok() {
            return create("org.projectlombok.lombok");
        }

    }

    public static class OrgSlf4jLibraryAccessors extends SubDependencyFactory {
        private final OrgSlf4jSlf4jLibraryAccessors laccForOrgSlf4jSlf4jLibraryAccessors = new OrgSlf4jSlf4jLibraryAccessors(owner);

        public OrgSlf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.slf4j.slf4j</b>
         */
        public OrgSlf4jSlf4jLibraryAccessors getSlf4j() {
            return laccForOrgSlf4jSlf4jLibraryAccessors;
        }

    }

    public static class OrgSlf4jSlf4jLibraryAccessors extends SubDependencyFactory {

        public OrgSlf4jSlf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>org.slf4j:slf4j-api</b> coordinates and
         * with version reference <b>org.slf4j.slf4j.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("org.slf4j.slf4j.api");
        }

        /**
         * Dependency provider for <b>simple</b> with <b>org.slf4j:slf4j-simple</b> coordinates and
         * with version reference <b>org.slf4j.slf4j.simple</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSimple() {
            return create("org.slf4j.slf4j.simple");
        }

    }

    public static class OrgYamlLibraryAccessors extends SubDependencyFactory {

        public OrgYamlLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>snakeyaml</b> with <b>org.yaml:snakeyaml</b> coordinates and
         * with version reference <b>org.yaml.snakeyaml</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSnakeyaml() {
            return create("org.yaml.snakeyaml");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final DnsjavaVersionAccessors vaccForDnsjavaVersionAccessors = new DnsjavaVersionAccessors(providers, config);
        private final ItVersionAccessors vaccForItVersionAccessors = new ItVersionAccessors(providers, config);
        private final JunitVersionAccessors vaccForJunitVersionAccessors = new JunitVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.dnsjava</b>
         */
        public DnsjavaVersionAccessors getDnsjava() {
            return vaccForDnsjavaVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.it</b>
         */
        public ItVersionAccessors getIt() {
            return vaccForItVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.junit</b>
         */
        public JunitVersionAccessors getJunit() {
            return vaccForJunitVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComGoogleVersionAccessors vaccForComGoogleVersionAccessors = new ComGoogleVersionAccessors(providers, config);
        private final ComGooglecodeVersionAccessors vaccForComGooglecodeVersionAccessors = new ComGooglecodeVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google</b>
         */
        public ComGoogleVersionAccessors getGoogle() {
            return vaccForComGoogleVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.googlecode</b>
         */
        public ComGooglecodeVersionAccessors getGooglecode() {
            return vaccForComGooglecodeVersionAccessors;
        }

    }

    public static class ComGoogleVersionAccessors extends VersionFactory  {

        private final ComGoogleCodeVersionAccessors$1 vaccForComGoogleCodeVersionAccessors$1 = new ComGoogleCodeVersionAccessors$1(providers, config);
        private final ComGoogleGuavaVersionAccessors vaccForComGoogleGuavaVersionAccessors = new ComGoogleGuavaVersionAccessors(providers, config);
        public ComGoogleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google.code</b>
         */
        public ComGoogleCodeVersionAccessors$1 getCode() {
            return vaccForComGoogleCodeVersionAccessors$1;
        }

        /**
         * Group of versions at <b>versions.com.google.guava</b>
         */
        public ComGoogleGuavaVersionAccessors getGuava() {
            return vaccForComGoogleGuavaVersionAccessors;
        }

    }

    public static class ComGoogleCodeVersionAccessors$1 extends VersionFactory  {

        private final ComGoogleCodeGsonVersionAccessors vaccForComGoogleCodeGsonVersionAccessors = new ComGoogleCodeGsonVersionAccessors(providers, config);
        public ComGoogleCodeVersionAccessors$1(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google.code.gson</b>
         */
        public ComGoogleCodeGsonVersionAccessors getGson() {
            return vaccForComGoogleCodeGsonVersionAccessors;
        }

    }

    public static class ComGoogleCodeGsonVersionAccessors extends VersionFactory  {

        public ComGoogleCodeGsonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.google.code.gson.gson</b> with value <b>2.10.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGson() { return getVersion("com.google.code.gson.gson"); }

    }

    public static class ComGoogleGuavaVersionAccessors extends VersionFactory  {

        public ComGoogleGuavaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.google.guava.guava</b> with value <b>33.1.0-jre</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGuava() { return getVersion("com.google.guava.guava"); }

    }

    public static class ComGooglecodeVersionAccessors extends VersionFactory  {

        private final ComGooglecodeJsonVersionAccessors vaccForComGooglecodeJsonVersionAccessors = new ComGooglecodeJsonVersionAccessors(providers, config);
        public ComGooglecodeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.googlecode.json</b>
         */
        public ComGooglecodeJsonVersionAccessors getJson() {
            return vaccForComGooglecodeJsonVersionAccessors;
        }

    }

    public static class ComGooglecodeJsonVersionAccessors extends VersionFactory  {

        private final ComGooglecodeJsonSimpleVersionAccessors vaccForComGooglecodeJsonSimpleVersionAccessors = new ComGooglecodeJsonSimpleVersionAccessors(providers, config);
        public ComGooglecodeJsonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.googlecode.json.simple</b>
         */
        public ComGooglecodeJsonSimpleVersionAccessors getSimple() {
            return vaccForComGooglecodeJsonSimpleVersionAccessors;
        }

    }

    public static class ComGooglecodeJsonSimpleVersionAccessors extends VersionFactory  {

        private final ComGooglecodeJsonSimpleJsonVersionAccessors vaccForComGooglecodeJsonSimpleJsonVersionAccessors = new ComGooglecodeJsonSimpleJsonVersionAccessors(providers, config);
        public ComGooglecodeJsonSimpleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.googlecode.json.simple.json</b>
         */
        public ComGooglecodeJsonSimpleJsonVersionAccessors getJson() {
            return vaccForComGooglecodeJsonSimpleJsonVersionAccessors;
        }

    }

    public static class ComGooglecodeJsonSimpleJsonVersionAccessors extends VersionFactory  {

        public ComGooglecodeJsonSimpleJsonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.googlecode.json.simple.json.simple</b> with value <b>1.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSimple() { return getVersion("com.googlecode.json.simple.json.simple"); }

    }

    public static class DnsjavaVersionAccessors extends VersionFactory  {

        public DnsjavaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>dnsjava.dnsjava</b> with value <b>3.5.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getDnsjava() { return getVersion("dnsjava.dnsjava"); }

    }

    public static class ItVersionAccessors extends VersionFactory  {

        private final ItUnimiVersionAccessors vaccForItUnimiVersionAccessors = new ItUnimiVersionAccessors(providers, config);
        public ItVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.it.unimi</b>
         */
        public ItUnimiVersionAccessors getUnimi() {
            return vaccForItUnimiVersionAccessors;
        }

    }

    public static class ItUnimiVersionAccessors extends VersionFactory  {

        private final ItUnimiDsiVersionAccessors vaccForItUnimiDsiVersionAccessors = new ItUnimiDsiVersionAccessors(providers, config);
        public ItUnimiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.it.unimi.dsi</b>
         */
        public ItUnimiDsiVersionAccessors getDsi() {
            return vaccForItUnimiDsiVersionAccessors;
        }

    }

    public static class ItUnimiDsiVersionAccessors extends VersionFactory  {

        public ItUnimiDsiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>it.unimi.dsi.fastutil</b> with value <b>8.5.13</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFastutil() { return getVersion("it.unimi.dsi.fastutil"); }

    }

    public static class JunitVersionAccessors extends VersionFactory  {

        public JunitVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>junit.junit</b> with value <b>4.13.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("junit.junit"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgJetbrainsVersionAccessors vaccForOrgJetbrainsVersionAccessors = new OrgJetbrainsVersionAccessors(providers, config);
        private final OrgJlineVersionAccessors vaccForOrgJlineVersionAccessors = new OrgJlineVersionAccessors(providers, config);
        private final OrgOw2VersionAccessors vaccForOrgOw2VersionAccessors = new OrgOw2VersionAccessors(providers, config);
        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSlf4jVersionAccessors vaccForOrgSlf4jVersionAccessors = new OrgSlf4jVersionAccessors(providers, config);
        private final OrgYamlVersionAccessors vaccForOrgYamlVersionAccessors = new OrgYamlVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.jetbrains</b>
         */
        public OrgJetbrainsVersionAccessors getJetbrains() {
            return vaccForOrgJetbrainsVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.jline</b>
         */
        public OrgJlineVersionAccessors getJline() {
            return vaccForOrgJlineVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.ow2</b>
         */
        public OrgOw2VersionAccessors getOw2() {
            return vaccForOrgOw2VersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.projectlombok</b>
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.slf4j</b>
         */
        public OrgSlf4jVersionAccessors getSlf4j() {
            return vaccForOrgSlf4jVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.yaml</b>
         */
        public OrgYamlVersionAccessors getYaml() {
            return vaccForOrgYamlVersionAccessors;
        }

    }

    public static class OrgJetbrainsVersionAccessors extends VersionFactory  {

        public OrgJetbrainsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.jetbrains.annotations</b> with value <b>24.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAnnotations() { return getVersion("org.jetbrains.annotations"); }

    }

    public static class OrgJlineVersionAccessors extends VersionFactory  {

        private final OrgJlineJlineVersionAccessors vaccForOrgJlineJlineVersionAccessors = new OrgJlineJlineVersionAccessors(providers, config);
        public OrgJlineVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.jline.jline</b>
         */
        public OrgJlineJlineVersionAccessors getJline() {
            return vaccForOrgJlineJlineVersionAccessors;
        }

    }

    public static class OrgJlineJlineVersionAccessors extends VersionFactory  {

        public OrgJlineJlineVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.jline.jline.reader</b> with value <b>3.26.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getReader() { return getVersion("org.jline.jline.reader"); }

    }

    public static class OrgOw2VersionAccessors extends VersionFactory  {

        private final OrgOw2AsmVersionAccessors vaccForOrgOw2AsmVersionAccessors = new OrgOw2AsmVersionAccessors(providers, config);
        public OrgOw2VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.ow2.asm</b>
         */
        public OrgOw2AsmVersionAccessors getAsm() {
            return vaccForOrgOw2AsmVersionAccessors;
        }

    }

    public static class OrgOw2AsmVersionAccessors extends VersionFactory  {

        public OrgOw2AsmVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.ow2.asm.asm</b> with value <b>9.7</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAsm() { return getVersion("org.ow2.asm.asm"); }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.projectlombok.lombok</b> with value <b>1.18.32</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSlf4jVersionAccessors extends VersionFactory  {

        private final OrgSlf4jSlf4jVersionAccessors vaccForOrgSlf4jSlf4jVersionAccessors = new OrgSlf4jSlf4jVersionAccessors(providers, config);
        public OrgSlf4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.slf4j.slf4j</b>
         */
        public OrgSlf4jSlf4jVersionAccessors getSlf4j() {
            return vaccForOrgSlf4jSlf4jVersionAccessors;
        }

    }

    public static class OrgSlf4jSlf4jVersionAccessors extends VersionFactory  {

        public OrgSlf4jSlf4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.slf4j.slf4j.api</b> with value <b>2.0.13</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("org.slf4j.slf4j.api"); }

        /**
         * Version alias <b>org.slf4j.slf4j.simple</b> with value <b>2.0.13</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSimple() { return getVersion("org.slf4j.slf4j.simple"); }

    }

    public static class OrgYamlVersionAccessors extends VersionFactory  {

        public OrgYamlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.yaml.snakeyaml</b> with value <b>2.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSnakeyaml() { return getVersion("org.yaml.snakeyaml"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
