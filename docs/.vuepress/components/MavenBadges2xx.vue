<template>
    <div>
        <MavenBadge2xx :artifact="'markwon'" />
        <MavenBadge2xx :artifact="'markwon-image-loader'" />
        <MavenBadge2xx :artifact="'markwon-syntax-highlight'"/>
        <MavenBadge2xx :artifact="'markwon-view'"/>
    </div>
</template>

<script>
import MavenBadge2xx from "./MavenBadge2xx.vue";
export default {
  name: "MavenBadges2xx",
  components: {
    MavenBadge2xx
  }
};
</script>


