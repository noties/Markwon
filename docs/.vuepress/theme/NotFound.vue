<template>
  <div class="theme-container">
    <div class="content">
      <template v-if="isSampleLink()">
        <h1>Markwon Sample</h1>
        <span>
          Download
          <a
            href="https://github.com/noties/Markwon/raw/sample-store/markwon-debug.apk"
          >sample app</a> and open
          <a :href="currentWindowLocation()" target="_blank">this link</a> again or try local one (if you already have app installed):
          <a
            :href="buildLocalDeeplink()"
          >{{ buildLocalDeeplink() }}</a>
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
      return /^.*\/app\/(sample|search).*$/.test(this.path());
    },
    currentWindowLocation() {
      return window && window.location.href;
    },
    buildLocalDeeplink() {
      const link = /.*\/app\/((?:sample|search).*)/.exec(this.path())[1];
      return "markwon://" + link;
    },
    path() {
      return this.$route.fullPath;
    }
  }
};
</script>
