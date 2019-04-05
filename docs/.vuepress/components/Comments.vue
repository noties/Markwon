<template></template>

<script>
export default {
  destroyed() {
    // remove comments section. It seems that utteranc does not support SPA
    const utterances = document.querySelectorAll(".utterances");
    const remove = e => e.parentElement.removeChild(e);

    if (!!utterances) {
      for (let i = 0; i < utterances.length; i++) {
        remove(utterances[i]);
      }
    }
  },
  mounted() {
    // this script adds HTML element and removes self
    const script = document.createElement("script");
    script.setAttribute("src", "https://utteranc.es/client.js");
    script.setAttribute("repo", "noties/Markwon");
    script.setAttribute("issue-term", "pathname");
    script.setAttribute("label", "doc-comments");
    script.setAttribute("theme", "github-light");
    script.setAttribute("crossorigin", "anonymous");
    script.setAttribute("async", true);

    const page = document.querySelectorAll(".page")[0];
    page.appendChild(script);
  }
};
</script>
