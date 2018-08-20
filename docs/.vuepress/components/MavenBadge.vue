<template>
    <a :href="mavenSearchUrl()"><img :src="shieldImgageUrl()" :alt="'' + artifact"></a>
</template>

<script>
export default {
    name: 'MavenBadge',
    props: ['artifact'],
    methods: {
        mavenSearchUrl: function() {
            return 'http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22' + this.artifact + '%22';
        },
        shieldImgageUrl: function() {
            return 'https://img.shields.io/maven-central/v/ru.noties/' + this.artifact +'.svg?label=' + this.artifact;
        }
    }
}
</script>

