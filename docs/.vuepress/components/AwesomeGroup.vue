<template>
  <div class="awesome-group">
    <div v-for="app in apps" class="awesome-app">
      <a :href="app.link" class="awesome-app-name" target="_blank" rel="noopener noreferrer">{{app.name}}&nbsp;<OutboundLink/></a>
      <img class="awesome-app-image" :src="app.image">
      <span v-if="app.description" class="awesome-app-description">{{ app.description }}</span>
    </div>
  </div>
</template>

<script>

import Link from './Link.vue'

export default {
  name: "AwesomeGroup",
  props: ["apps"],
  components: {
      Link
  }
};
</script>

<style scoped>
.awesome-group {
  display: flex;
  flex-wrap: wrap;
}
.awesome-app {
  padding: 1em;
  background-color: #fff;
  margin: 0.25em;
  border-radius: 0.25em;
  box-shadow: 0 0 0.1em 0.1em #eee;
  max-width: 30%;
  min-width: 100px;
  display: flex;
  align-items: center;
  flex-direction: column;
}
.awesome-app-name {
  font-size: 1em;
  font-weight: 500;
  margin-bottom: 0.5em;
}
.awesome-app-image {
  width: 96px;
  height: 96px;
  display: block;
}
.awesome-app-description {
  margin-top: 1em;
  font-size: 0.85em;
}
</style>


