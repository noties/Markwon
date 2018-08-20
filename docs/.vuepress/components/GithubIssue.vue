<template>
    <a :href="githubIssueHref">{{linkContent}}</a>
</template>

<script>
export default {
  name: "GithubIssue",
  props: {
    id: {
      required: true
    },
    displayName: { required: false },
    user: {
      default: "noties",
      required: false
    },
    repo: {
      default: "Markwon",
      required: false
    }
  },
  computed: {
    githubIssueHref: function() {
      return (
        "https://github.com/" + this.user + "/" + this.repo + "/issues/" + this.id
      );
    },
    linkContent: function() {
      return this.displayName || "#" + this.id;
    }
  }
};
</script>

