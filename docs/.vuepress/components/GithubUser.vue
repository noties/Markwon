<template>
    <a :href="githubUserProfileHref" target="_blank" rel="noopener noreferrer"><b>{{linkContent}}</b><OutboundLink/></a>
</template>

<script>
export default {
  name: "GithubUser",
  props: {
    name: { required: true }
  },
  computed: {
    githubUserProfileHref: function() {
      return "https://github.com/" + this.name;
    },
    linkContent: function() {
      return "@" + this.name;
    }
  }
};
</script>

