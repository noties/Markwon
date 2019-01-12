<template>
    <a :href="mavenSearchUrl()"><img :src="shieldImgageUrl()" :alt="displayLabel"></a>
</template>

<script>
export default {
    name: 'MavenBadge2xx',
    props: ['artifact', 'label'],
    methods: {
        mavenSearchUrl: function() {
            return `http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22${this.artifact}%22`;
        },
        shieldImgageUrl: function() {
            return `https://img.shields.io/maven-central/v/ru.noties/${this.artifact}.svg?label=${this.displayLabel}`;
        }
    },
    computed: {
        displayLabel() {
            return this.label || this.artifact;
        }
    }
}
</script>

