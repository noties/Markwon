<template>
    <a :href="linkHref()" target="_blank" rel="noopener noreferrer">{{linkText()}}<OutboundLink/></a>
</template>

<script>
var map = {
  "commonmark-spec": {
    displayName: "commonmark spec",
    href: "https://spec.commonmark.org/0.28/"
  },
  "commonmark-spec#inline": {
      href: "https://spec.commonmark.org/0.28/#raw-html"
  },
  "commonmark-spec#block": {
      href: "https://spec.commonmark.org/0.28/#html-blocks"
  },
  "commonmark-spec#soft-break": {
      href: "https://spec.commonmark.org/0.28/#soft-line-breaks"
  },
  "commonmark-dingus": {
      displayName: "commonmark dingus",
      href: "https://spec.commonmark.org/dingus/"
  },
  "html-inlines": {
      href: "https://developer.mozilla.org/en-US/docs/Web/HTML/Inline_elements"
  },
  "html-blocks": {
      href: "https://developer.mozilla.org/en-US/docs/Web/HTML/Block-level_elements"
  },
  "jsoup": {
      displayName: "Jsoup",
      href: "https://github.com/jhy/jsoup/"
  },
  "markwon-jsoup": {
      href: "https://github.com/noties/Markwon/tree/master/markwon-html-parser-impl/src/main/java/ru/noties/markwon/html/impl/jsoup"
  },
  "commonmark-java": {
      href: "https://github.com/atlassian/commonmark-java/",
      displayName: "commonmark-java"
  }
};

export default {
  name: "Link",
  props: ["name", "displayName", "href"],
  methods: {
    linkHref: function() {
      return this.href || map[this.name].href;
    },
    linkText: function() {
        return this.displayName || map[this.name].displayName;
    }
  }
};
</script>

