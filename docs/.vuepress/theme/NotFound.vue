<template>
  <div class="theme-container">
    <div class="content">
      <template v-if="isSampleLink()">
        <h1>Markwon Sample</h1>
        <span>
          Download
          <a href="https://github.com/noties/Markwon/raw/sample-store/markwon-debug.apk">sample app</a> and open
          <a :href="currentWindowLocation()">this link</a> again
        </span>
      </template>
      <template v-else>
        <h1>404</h1>
        <blockquote>{{ getMsg() }}</blockquote>
        <router-link to="/">Take me home.</router-link>
      </template>
    </div>
  </div>
</template>

<script>
const msgs = [
  `There's nothing here.`,
  `How did we get here?`,
  `That's a Four-Oh-Four.`,
  `Looks like we've got some broken links.`
];

export default {
  methods: {
    getMsg() {
      return msgs[Math.floor(Math.random() * msgs.length)];
    },
    isSampleLink() {
      const path = this.$router.currentRoute.path;
      return /^\/app\/(sample|search).*$/.test(path);
    },
    currentWindowLocation() {
      return window.location.href
    }
  }
};
</script>
