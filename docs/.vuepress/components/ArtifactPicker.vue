<template>
  <div>
    <div class="artifact-container">
      <div v-for="artifact in artifacts" class="artifact" @click="toggleSelection(artifact)">
        <div class="artifact-header">
          <input type="checkbox" v-model="selected" :value="artifact.id" :id="artifact.id">
          <strong>
            <label :for="artifact.id">{{artifact.name}}</label>
          </strong>
        </div>
        <div class="artifact-description" v-if="artifact.description">{{artifact.description}}</div>
      </div>
    </div>
    <div class="extra-class language-gradle selected-artifacts" v-if="selected.length > 0">
      <div class="selected-artifact-script">
        <span class="token keyword">final def</span>
        <span>&nbsp;markwon_version =&nbsp;</span>
        <span class="token string">'{{latestVersion}}'</span>
      </div>
      <br>
      <div class="selected-artifact-script" v-for="artifact in selectedArtifacts">
        <span>implementation&nbsp;</span>
        <span class="token string">"{{artifact.group}}:{{artifact.id}}:</span>
        <span>$markwon_version</span>
        <span class="token string">"</span>
      </div>
    </div>
  </div>
</template>

<script>
import { artifacts } from "../.artifacts.v3.js";

if (!artifacts) {
  throw "Artifacts not found. Use `collectArtifacts.js` script to obtain artifacts metadata.";
}

export default {
  name: "ArtifactPicker",
  data() {
    return {
      artifacts,
      selected: ["core"],
      latestVersion: "latest_version"
    };
  },
  methods: {
    toggleSelection(artifact) {
      const index = this.selected.indexOf(artifact.id);
      if (index < 0) {
        this.selected.push(artifact.id);
      } else {
        this.selected.splice(index, 1);
      }
    }
  },
  computed: {
    selectedArtifacts() {
      return this.artifacts.filter(a => this.selected.indexOf(a.id) >= 0);
    }
  }
};
</script>

<style scoped>
.artifact-container {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  margin-top: 0.5em;
}
.artifact {
  flex: 1;
  border: 1px #ccc solid;
  background-color: #fafafa;
  padding: 0.5em;
  margin: 0.2em;
  border-radius: 0.25em;
  min-width: 10em;
  max-width: 10em;
}
.artifact-description {
  font-size: 0.85em;
  margin-top: 0.5em;
}
.selected-artifacts {
  color: white;
  font-family: source-code-pro, Menlo, Monaco, Consolas, "Courier New",
    monospace;
  padding: 16px;
  text-align: left;
  word-spacing: normal;
  word-break: normal;
  word-wrap: normal;
  line-height: 1.5;
  -moz-tab-size: 4;
  hyphens: none;
  font-size: 0.85em;
  margin-top: 0.5em;
}
.selected-artifact-script {
  display: flex;
  flex-wrap: wrap;
}
</style>