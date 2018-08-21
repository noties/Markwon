<template>
    <table>
        <tr><td><b>name</b></td><td><code>{{name}}</code></td></tr>
        <tr><td><b>type</b></td><td><code>{{type}}</code></td></tr>
        <tr><td><b>default</b></td><td v-html="defaults"></td></tr>
    </table>
</template>

<script>
export default {
  name: "ThemeProperty",
  props: ["name", "type", "defaults"]
};
</script>

