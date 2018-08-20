<template>
    <div>
        <MavenBadge :artifact="'markwon'" />
        <MavenBadge :artifact="'markwon-image-loader'" />
        <MavenBadge :artifact="'markwon-syntax-highlight'"/>
        <MavenBadge :artifact="'markwon-view'"/>
    </div>
</template>

<script>
import MavenBadge from "./MavenBadge.vue";
export default {
  name: "MavenBadges",
  components: {
    MavenBadge
  }
};
</script>


