<template>
    <a :href="githubPullHref" target="_blank" rel="noopener noreferrer">{{linkContent}}<OutboundLink/></a>
</template>

<script>
export default {
  name: "GithubPull",
  props: {
    id: { required: true },
    user: {
        default: 'noties'
    },
    repo: {
        default: 'Markwon'
    }
  },
  computed: {
      githubPullHref: function() {
        return "https://github.com/" + this.user + "/" + this.repo + "/pull/" + this.id
      },
      linkContent: function() {
          return "#" + this.id;
      }
  }
};
</script>

