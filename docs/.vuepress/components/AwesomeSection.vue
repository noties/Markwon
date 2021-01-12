<template>
  <div>
    <AwesomeGroup :apps="apps" />
  </div>
</template>

<script>
import AwesomeGroup from "./AwesomeGroup.vue";
export default {
  name: "AwesomeSection",
  components: {
    AwesomeGroup,
  },
  computed: {
    apps: function () {
      return [
        {
          name: "Nextcloud",
          image: "/assets/apps/nextcloud.png",
          link: "https://github.com/nextcloud/android",
          description:
            "A safe home for all your data. Access & share your files, calendars, contacts, mail & more from any device, on your terms.",
        },
        {
          name: "Habitica",
          image: "/assets/apps/habitica.png",
          link:
            "https://play.google.com/store/apps/details?id=com.habitrpg.android.habitica",
          description:
            "Treat your life like a game to stay motivated and organized! Habitica makes it simple to have fun while accomplishing goals.",
        },
        {
          name: "Cinopsys: Movies and Shows",
          image: "/assets/apps/cinopsys.png",
          link:
            "https://play.google.com/store/apps/details?id=com.cinopsys.movieshows",
        },
        {
          name: "Pure Writer",
          image: "/assets/apps/purewriter.png",
          link:
            "https://play.google.com/store/apps/details?id=com.drakeet.purewriter",
          description: "Never lose content editor & Markdown",
        },
        {
          name: "Stack",
          image: "/assets/apps/stack.png",
          link: "https://github.com/tylerbwong/stack",
          description:
            "An Android app for browsing Stack Overflow and other Stack Exchange sites",
        },
        {
          name: "Infinity for Reddit",
          image: "/assets/apps/infinity.webp",
          link: "https://play.google.com/store/apps/details?id=ml.docilealligator.infinityforreddit",
          description: " kjskjs dhfj ",
        },
      ];
    },
  },
};
</script>