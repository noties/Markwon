<template>
  <div>
    <div class="container">
      <div class="container-item">
        <textarea @input="processMarkdown">{{markdownInput}}</textarea>
      </div>
      <div class="container-item display" v-html="markdownHtml"></div>
    </div>
    <div class="footer">
      <!-- <p v-if="permalink">
        Permalink: <span v-html="permalink"></span>
      </p> -->
      <p>
        <em>
          * Please note that this tool can be used to evaluate how commonmark
          will react to certain markdown input. There is no guarantee that results
          here and in an Android application that uses Markwon will be the same.
          Especially if raw HTML is involved.
        </em>
      </p>
      <p>
        <em>
          ** For a more sophisticated commonmark sandbox editor
          <a
            href="https://spec.commonmark.org/dingus/"
          >the dingus</a> can be used.
        </em>
      </p>
    </div>
  </div>
</template>

<script>
import commonmark from "commonmark";

const parser = new commonmark.Parser();
const writer = new commonmark.HtmlRenderer();

export default {
  name: "CommonmarkSandbox",
  data() {
    return {
      markdownInput: this.initialMarkdown()
    };
  },
  methods: {
    initialMarkdown() {
      // const query = this.$route.query;
      // if (query) {
      //   const md = query.md;
      //   if (md) {
      //     query.md = null;
      //     return md;
      //   }
      // }
      return `# Header 1\n\n*Hello* __there!__`;
    },
    processMarkdown(e) {
      this.markdownInput = e.target.value;
    }
  },
  computed: {
    markdownHtml() {
      return writer.render(parser.parse(this.markdownInput));
    },
    // permalink() {
    //   if (!this.markdownInput) {
    //       return null;
    //   }
    //   const url = `${window.location.href}?md=${encodeURIComponent(this.markdownInput)}`;
    //   return `<a href="#" title="${url}" onclick="">click to copy</a>`;
    // }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: nowrap;
  box-sizing: border-box;
}
.container-item {
  flex: 4;
  padding: 0.5em;
}
.container textarea {
  width: 100%;
  height: 100%;
  resize: vertical;
  min-height: 20em;
  padding: 0px;
  margin: 0px;
}
.display {
  flex: 5;
  background-color: rgba(0, 0, 0, 0.05);
}
.footer {
  color: #666;
  font-size: 0.85em;
}
</style>


